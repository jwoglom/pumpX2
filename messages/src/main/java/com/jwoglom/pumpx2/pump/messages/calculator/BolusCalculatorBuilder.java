package com.jwoglom.pumpx2.pump.messages.calculator;

import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BolusCalcDataSnapshotResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBGResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A builder object for filling data into {@link BolusCalculator} when response messages are received
 * from the pump.
 */
public class BolusCalculatorBuilder {
    // arguments
    private BolusCalcDataSnapshotResponse dataSnapshotResponse;
    private LastBGResponse lastBGResponse;

    private BolusCalcDataSnapshot dataSnapshot;
    private BolusCalcLastBG lastBG;
    private BolusParameters userInputParameters = new BolusParameters();

    /**
     * When no messages have been received
     */
    public BolusCalculatorBuilder() {
    }

    /**
     * When a BolusCalcDataSnapshotResponse and LastBGResponse have been received
     */
    public BolusCalculatorBuilder(BolusCalcDataSnapshotResponse dataSnapshotResponse, LastBGResponse lastBGResponse) {
        this.dataSnapshotResponse = dataSnapshotResponse;
        this.lastBGResponse = lastBGResponse;
        this.dataSnapshot = new BolusCalcDataSnapshot(dataSnapshotResponse);
        this.lastBG = new BolusCalcLastBG(dataSnapshotResponse, lastBGResponse);
    }

    /**
     * When a BolusCalcDataSnapshotResponse is received
     */
    public void onBolusCalcDataSnapshotResponse(BolusCalcDataSnapshotResponse resp) {
        dataSnapshotResponse = resp;
        dataSnapshot = new BolusCalcDataSnapshot(dataSnapshotResponse);
        if (lastBG == null && lastBGResponse != null) {
            lastBG = new BolusCalcLastBG(dataSnapshotResponse, lastBGResponse);
        }
    }

    /**
     * When a LastBGResponse is received
     */
    public void onLastBGResponse(LastBGResponse resp) {
        lastBGResponse = resp;
        if (dataSnapshotResponse != null) {
            lastBG = new BolusCalcLastBG(dataSnapshotResponse, lastBGResponse);
        }
    }

    /**
     * @return the set BG if filled by the user, otherwise the filled-in BG if able
     */
    public Optional<Integer> getGlucoseMgdl() {
        if (userInputParameters.getGlucoseMgdl() != null) {
            return Optional.of(userInputParameters.getGlucoseMgdl());
        }

        if (lastBG == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(lastBG.determineLastBG().getLeft());
    }

    /**
     * @param glucoseMgdl the BG value in mg/dL set by the user
     */
    public void setGlucoseMgdl(Integer glucoseMgdl) {
        userInputParameters = userInputParameters.withGlucoseMgdl(glucoseMgdl);
    }

    /**
     * @return the set number of carbs in grams if filled by the user
     */
    public Optional<Integer> getCarbsValueGrams() {
        return Optional.ofNullable(userInputParameters.getCarbsGrams());
    }

    /**
     * @param carbsGrams the number of carbs in grams set by the user
     */
    public Optional<BolusCalcCondition> setCarbsValueGrams(Integer carbsGrams) {
        if (dataSnapshot != null && !dataSnapshot.carbEntryEnabled) {
            return Optional.of(new BolusCalcCondition.FailedPrecondition("carb entry disabled"));
        }

        userInputParameters = userInputParameters.withCarbsGrams(carbsGrams);
        return Optional.empty();
    }

    /**
     * @return a {@link BolusCalcUnits} object representing the total insulin units calculated
     */
    public BolusCalcUnits getInsulinUnits() {
        return new BolusCalculator(userInputParameters, dataSnapshot, lastBG).getUnits();
    }

    /**
     * @return a set of {@link BolusCalcCondition} objects describing why the bolus calculator
     * decision was made.
     */
    public Set<BolusCalcCondition> getConditions() {
        return new BolusCalculator(userInputParameters, dataSnapshot, lastBG).getConditions();
    }

    /**
     * @param insulinUnits the override number of units of insulin set by the user
     */
    public void setInsulinUnits(Double insulinUnits) {
        userInputParameters = userInputParameters.withUnits(insulinUnits);
    }

    /**
     * @return a {@link BolusCalculator} which can be used to get units and conditions
     */
    public BolusCalculator build() {
        return new BolusCalculator(userInputParameters, dataSnapshot, lastBG);
    }
}
