package com.jwoglom.pumpx2.pump.messages.calculator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.messages.models.InsulinUnit;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains logic for deciding an estimated bolus amount given inputs and current pump/CGM status.
 */
public class BolusCalculator {
    private static final String TAG = "BolusCalculator";

    // input fields
    private final BolusParameters userInputParameters;
    private final BolusCalcDataSnapshot dataSnapshot;
    private final BolusCalcLastBG lastBG;

    BolusCalculator(BolusParameters userInputParameters, BolusCalcDataSnapshot dataSnapshot, BolusCalcLastBG lastBG) {
        this.userInputParameters = userInputParameters;
        this.dataSnapshot = dataSnapshot;
        this.lastBG = lastBG;
    }

    /**
     * @return the {@link BolusCalcComponent} representing insulin for carbs
     */
    public BolusCalcComponent getAddedFromCarbs() {
        double addedFromCarbs = 0;
        List<BolusCalcCondition> conditions = new ArrayList<>();

        if (dataSnapshot == null) {
            return zeroWithCondition(new BolusCalcCondition.WaitingOnPrecondition("carb ratio from data snapshot"));
        }

        if (userInputParameters.carbsGrams == null) {
            return zeroWithCondition(new BolusCalcCondition.NonActionDecision("add units for carbs", "no carbs provided"));
        }

        double ratio = InsulinUnit.from1000To1(dataSnapshot.carbRatio);
        if (ratio > 0) {
            addedFromCarbs = userInputParameters.carbsGrams / ratio;
        } else {
            conditions.add(new BolusCalcCondition.FailedSanityCheck("raw carb ratio is invalid: " + dataSnapshot.carbRatio));
        }

        return new BolusCalcComponent(BolusCalcUnits.doublePrecision(addedFromCarbs), conditions);
    }

    /**
     * @return the {@link BolusCalcComponent} representing insulin for the current BG correction
     */
    public BolusCalcComponent getAddedFromGlucose() {
        double addedFromBG = 0;

        if (dataSnapshot == null) {
            return zeroWithCondition(new BolusCalcCondition.WaitingOnPrecondition("data snapshot"));
        }

        if (lastBG == null) {
            return zeroWithCondition(new BolusCalcCondition.WaitingOnPrecondition("last BG"));
        }

        Integer lastBGValue = null;
        List<BolusCalcCondition> conditions = new ArrayList<>();
        if (userInputParameters.glucoseMgdl != null) {
            lastBGValue = userInputParameters.glucoseMgdl;
            conditions.add(new BolusCalcCondition.DataDecision("use BG user input"));
        } else {
            Pair<Integer, List<BolusCalcCondition>> lastBG = this.lastBG.determineLastBG();
            conditions = new ArrayList<>(lastBG.getRight());

            if (lastBG.getLeft() == null) {
                conditions.add(new BolusCalcCondition.NonActionDecision("add units for BG", "no BG provided"));
                return new BolusCalcComponent(0.0, conditions);
            }
            lastBGValue = lastBG.getLeft();
        }

        if (dataSnapshot.targetBg < 40 || dataSnapshot.targetBg > 400) {
            conditions.add(new BolusCalcCondition.FailedSanityCheck("target BG is out of range or empty in pump profile"));
            return new BolusCalcComponent(0.0, conditions);
        }

        if (dataSnapshot.isf <= 0) {
            conditions.add(new BolusCalcCondition.FailedSanityCheck("no Insulin Sensitivity Factor present in pump profile"));
            return new BolusCalcComponent(0.0, conditions);
        }

        int bgDiff = lastBGValue - dataSnapshot.targetBg;
        addedFromBG = (1.0 * bgDiff) / dataSnapshot.isf;

//        if (addedInsulin > 0) {
//            conditions.add(new BolusCalcCondition.Decision("add units for BG", "BG is above target"));
//        } else if (addedInsulin < 0) {
//            conditions.add(new BolusCalcCondition.Decision("subtract units for BG", "BG is below target"));
//        } else {
//            conditions.add(new BolusCalcCondition.NonActionDecision("adjust units for BG", "BG is at target"));
//        }

        return new BolusCalcComponent(BolusCalcUnits.doublePrecision(addedFromBG), conditions);
    }

    /**
     * @return the {@link BolusCalcComponent} representing insulin for the current IOB correction
     */
    public BolusCalcComponent getAddedFromIOB() {
        double addedFromIOB = 0;
        List<BolusCalcCondition> conditions = new ArrayList<>();

        if (dataSnapshot == null) {
            return zeroWithCondition(new BolusCalcCondition.WaitingOnPrecondition("data snapshot"));
        }

        if (dataSnapshot.iob > 0) {
            addedFromIOB = -1 * dataSnapshot.iob;
        }

        return new BolusCalcComponent(BolusCalcUnits.doublePrecision(addedFromIOB), conditions);
    }

    /**
     * @return a {@link BolusCalcDecision} including the total generated insulin amount, the amounts
     * for the individual {@link BolusCalcComponent}s, and the conditions which resulted in this calculation.
     */
    public BolusCalcDecision parse() {
        if (userInputParameters.units != null) {
            return new BolusCalcDecision(BolusCalcUnits.fromUser(userInputParameters.units), ImmutableList.of(new BolusCalcCondition.Decision("use user-provided insulin amount")));
        }

        List<BolusCalcCondition> conditions = new ArrayList<>();

        BolusCalcComponent addedFromCarbs = getAddedFromCarbs();
        BolusCalcComponent addedFromBG = getAddedFromGlucose();
        BolusCalcComponent addedFromIOB = getAddedFromIOB();


        L.d(TAG, "addedFromCarbs: " + addedFromCarbs);
        L.d(TAG, "addedFromBG: " + addedFromBG);
        L.d(TAG, "addedFromIOB: " + addedFromIOB);

        double total = addedFromCarbs.getUnits();
//        boolean includeBG = true;
//        boolean includeIOB = true;
//
//        // when above or at target BG, do not include IOB
//        if (addedFromBG.getUnits() >= 0 && addedFromBG.getUnits() + addedFromIOB.getUnits() <= 0) {
//            conditions.add(new BolusCalcCondition.Decision("not reduce insulin", "IOB amount is greater than carb correction"));
//            includeIOB = false;
//        }
//
//
////        // when below target BG, don't include IOB in total
////        if (addedFromBG.getUnits() <= 0) {
//////            includeIOB = false;
//////            conditions.add(new BolusCalcCondition.Decision("not reduce insulin by IOB", "BG is not above target"));
////        }
//
//        if (includeBG) {
//            total += addedFromBG.getUnits();
//        }
//
//        if (includeIOB) {
//            total += addedFromIOB.getUnits();
//        }
//
//        if (total < 0.0) {
//            conditions.add(new BolusCalcCondition.Decision("set zero insulin", "negative correction without IOB greater than carb amount")); // "while below target"
//            total = 0.0;
//        }


//        if (addedFromBG.getUnits() + addedFromIOB.getUnits() < 0) {
//            if (addedFromBG.getUnits() > 0) {
//                conditions.add(new BolusCalcCondition.Decision("not add positive BG correction", "active IOB is greater than correction bolus while above target"));
//            } else if (addedFromBG.getUnits() <= 0) {
//                if (total + addedFromBG.getUnits() <= 0) {
//                    conditions.add(new BolusCalcCondition.Decision("set zero insulin", "negative correction without IOB greater than carb amount while below target"));
//                    total = 0.0;
//                } else {
//                    conditions.add(new BolusCalcCondition.Decision("not reduce insulin by IOB", "BG is not above target"));
//                    total += addedFromBG.getUnits();
//                }
//            } else if (total + addedFromBG.getUnits() + addedFromIOB.getUnits() <= 0) {
//                conditions.add(new BolusCalcCondition.Decision("set zero insulin", "negative correction greater than carb amount while below target"));
//                total = 0.0;
//            } else {
//                total += addedFromBG.getUnits() + addedFromIOB.getUnits();
//            }
//        } else {
//            total += addedFromBG.getUnits() + addedFromIOB.getUnits();
//        }

        // above or at target
        if (addedFromBG.getUnits() >= 0) {
            // iob is higher than correction, so don't add either (carbs + max(0, bg + iob))
            if (addedFromBG.getUnits() + addedFromIOB.getUnits() < 0) {
                conditions.add(new BolusCalcCondition.Decision("not add positive BG correction", "active IOB is greater than correction bolus while above target"));
            } else if (addedFromBG.getUnits() + addedFromIOB.getUnits() == 0) {
                // do nothing
            } else {
                // correction + iob adds a positive delta to the insulin
                conditions.add(new BolusCalcCondition.Decision("add positive BG correction", "above BG target"));
                total += addedFromBG.getUnits() + addedFromIOB.getUnits();
            }
        } else { // below target
            if (addedFromBG.getUnits() + addedFromIOB.getUnits() == 0) {
                // do nothing
            } else if (total + addedFromBG.getUnits() + addedFromIOB.getUnits() > 0) { // correction + iob is less than the carb amount
                conditions.add(new BolusCalcCondition.Decision("add negative BG correction", "below BG target"));
                total += addedFromBG.getUnits() + addedFromIOB.getUnits();
            } else { // correction + iob takes the insulin amount negative
                total = 0.0;
                conditions.add(new BolusCalcCondition.Decision("set zero insulin", "negative correction greater than carb amount"));
            }
        }
//
//        if (addedFromBG.getUnits() + addedFromIOB.getUnits() < 0) {
//            if (addedFromBG.getUnits() >= 0) {
//                conditions.add(new BolusCalcCondition.Decision("not add positive BG correction", "active IOB is greater than correction bolus while above target"));
//            } else if (total + addedFromBG.getUnits() + addedFromIOB.getUnits() > 0) {
//                total += addedFromBG.getUnits() + addedFromIOB.getUnits();
//            } else {
//                conditions.add(new BolusCalcCondition.Decision("set zero insulin", "negative correction greater than carb amount"));
//            }
//        } else {
//            total += addedFromBG.getUnits() + addedFromIOB.getUnits();
//        }


        conditions.addAll(addedFromCarbs.getConditions());
        conditions.addAll(addedFromBG.getConditions());
        conditions.addAll(addedFromIOB.getConditions());

        BolusCalcUnits bolusCalcUnits = new BolusCalcUnits(total, addedFromCarbs.getUnits(), addedFromBG.getUnits(), addedFromIOB.getUnits(), 0);

//        if (addedFromCarbs.getUnits() == 0) {
//            if (addedFromBG.getUnits() + addedFromIOB.getUnits() > 0) {
//                conditions.add(new BolusCalcCondition.Decision("increase insulin", "BG is over target"));
////            } else if (addedFromBG.getUnits() > 0 && (addedFromBG.getUnits() + addedFromIOB.getUnits() < 0)) {
////                conditions.add(new BolusCalcCondition.Decision("reduce insulin", "IOB is greater than BG correction despite being over target BG"));
////            } else if (addedFromBG.getUnits() < 0) {
////                conditions.add(new BolusCalcCondition.Decision("reduce insulin", "IOB is greater than BG correction despite being over target BG"));
//            }
//        } else {
//            if (addedFromBG.getUnits() + addedFromIOB.getUnits() > 0) {
//                conditions.add(new BolusCalcCondition.Decision("increase insulin", "BG is over target"));
////            } else if (addedFromBG.getUnits() > 0 && (addedFromBG.getUnits() + addedFromIOB.getUnits() < 0)) {
////                conditions.add(new BolusCalcCondition.Decision("reduce insulin", "IOB reduces correction despite being over target BG"));
//            } else if (addedFromBG.getUnits() + addedFromIOB.getUnits() < 0) {
//                conditions.add(new BolusCalcCondition.Decision("reduce insulin", "BG is under target"));
//            }
//        }

        L.d(TAG, "bolusCalculator: " + bolusCalcUnits + ", " + conditions);
        Preconditions.checkState(bolusCalcUnits.getTotal() >= 0);
        return new BolusCalcDecision(bolusCalcUnits, conditions);
    }

    /**
     * @return the {@link BolusCalcUnits} representing the total estimated amount of insulin and the
     * components which resulted in this estimation
     */
    public BolusCalcUnits getUnits() {
        return parse().getUnits();
    }

    /**
     * @return a list of {@link BolusCalcCondition}s which contain human-readable descriptions of
     * how and why this estimate was made
     */
    public List<BolusCalcCondition> getConditions() {
        return parse().getConditions();
    }

    /**
     * @return true if the user inputted a manual insulin amount, and that is being used instead of
     * the suggested amount from the bolus calculation
     */
    public boolean isOverriddenByUser() {
        return userInputParameters.units != null;
    }

    private static BolusCalcComponent zeroWithCondition(BolusCalcCondition one, BolusCalcCondition... more) {
        return new BolusCalcComponent(0.0, ImmutableList.<BolusCalcCondition>builder().add(one).addAll(Arrays.asList(more)).build());
    }
}
