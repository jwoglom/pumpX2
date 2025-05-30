package com.jwoglom.pumpx2.pump.messages.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.Validate;
import java.util.Arrays;
import com.jwoglom.pumpx2.pump.messages.models.InsulinUnit;
import com.jwoglom.pumpx2.shared.L;
import org.apache.commons.lang3.tuple.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Contains logic for deciding an estimated bolus amount given inputs and current pump/CGM status.
 */
public class BolusCalculator {
    private static final Logger log = LoggerFactory.getLogger(BolusCalculator.class);

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
            return zeroWithCondition(new BolusCalcCondition.NonActionDecision("not adding units for carbs", "no carbs provided"));
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
            conditions.add(new BolusCalcCondition.DataDecision("using BG user input"));
        } else {
            Pair<Integer, List<BolusCalcCondition>> lastBG = this.lastBG.determineLastBG();
            conditions = new ArrayList<>(lastBG.getRight());

            if (lastBG.getLeft() == null) {
                conditions.add(new BolusCalcCondition.NonActionDecision("not adding units for BG", "no BG provided"));
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
    public BolusCalcDecision parse(BolusCalcCondition ...ignoredConditions) {
        if (userInputParameters.units != null) {
            return new BolusCalcDecision(
                    BolusCalcUnits.fromUser(userInputParameters.units),
                    Set.of(new BolusCalcCondition.DataDecision("using user-provided insulin amount")));
        }

        Set<BolusCalcCondition> ignored = Arrays.stream(ignoredConditions).collect(Collectors.toSet());

        Set<BolusCalcCondition> conditions = new TreeSet<>();

        BolusCalcComponent addedFromCarbs = getAddedFromCarbs();
        BolusCalcComponent addedFromBG = getAddedFromGlucose();
        BolusCalcComponent addedFromIOB = getAddedFromIOB();

        log.debug("addedFromCarbs: " + addedFromCarbs);
        log.debug("addedFromBG: " + addedFromBG);
        log.debug("addedFromIOB: " + addedFromIOB);

        double total = addedFromCarbs.getUnits();

        // above or at target
        if (addedFromBG.getUnits() >= 0) {
            // iob is higher than correction, so don't add either (carbs + max(0, bg + iob))
            if (addedFromBG.getUnits() + addedFromIOB.getUnits() < 0) {
                conditions.add(BolusCalcCondition.NO_POSITIVE_BG_CORRECTION);
            } else if (addedFromBG.getUnits() + addedFromIOB.getUnits() == 0) {
                // do nothing
            } else {
                // correction + iob adds a positive delta to the insulin
                if (shouldApply(ignored, conditions, BolusCalcCondition.POSITIVE_BG_CORRECTION)) {
                    total += addedFromBG.getUnits() + addedFromIOB.getUnits();
                }
            }
        } else { // below target
            if (addedFromBG.getUnits() + addedFromIOB.getUnits() == 0) {
                // do nothing
            } else if (total + addedFromBG.getUnits() + addedFromIOB.getUnits() > 0) { // correction + iob is less than the carb amount
                if (shouldApply(ignored, conditions, BolusCalcCondition.NEGATIVE_BG_CORRECTION)) {
                    total += addedFromBG.getUnits() + addedFromIOB.getUnits();
                }
            } else { // correction + iob takes the insulin amount negative
                if (addedFromCarbs.getUnits() == 0) {
                    // zero carbs, so don't apply condition
                    total = 0.0;
                } else if (shouldApply(ignored, conditions, BolusCalcCondition.SET_ZERO_INSULIN)) {
                    total = 0.0;
                }
            }
        }

        conditions.addAll(addedFromCarbs.getConditions());
        conditions.addAll(addedFromBG.getConditions());
        conditions.addAll(addedFromIOB.getConditions());

        BolusCalcUnits bolusCalcUnits = new BolusCalcUnits(total, addedFromCarbs.getUnits(), addedFromBG.getUnits(), addedFromIOB.getUnits(), 0);

        log.debug("bolusCalculator: " + bolusCalcUnits + ", " + conditions);
        Validate.isTrue(bolusCalcUnits.getTotal() >= 0);

        // If there are any FailedPreconditions, then return 0 units regardless
        for (BolusCalcCondition condition : conditions) {
            if (condition instanceof BolusCalcCondition.FailedSanityCheck) {
                return new BolusCalcDecision(BolusCalcUnits.fromUser(0), conditions);
            }
        }

        return new BolusCalcDecision(bolusCalcUnits, conditions);
    }

    private boolean shouldApply(Set<BolusCalcCondition> ignored, Set<BolusCalcCondition> conditions, BolusCalcCondition condition) {
        if (ignored.contains(condition)) {
            conditions.add(new BolusCalcCondition.IgnoredDecision(condition));
            return false;
        } else {
            conditions.add(condition);
            return true;
        }
    }

    /**
     * @return the {@link BolusCalcUnits} representing the total estimated amount of insulin and the
     * components which resulted in this estimation
     */
    public BolusCalcUnits getUnits() {
        return parse().getUnits();
    }

    /**
     * @return a set of {@link BolusCalcCondition}s which contain human-readable descriptions of
     * how and why this estimate was made
     */
    public Set<BolusCalcCondition> getConditions() {
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
        List<BolusCalcCondition> list = new ArrayList<>();
        list.add(one);
        list.addAll(Arrays.asList(more));

        return new BolusCalcComponent(0.0, list);
    }
}
