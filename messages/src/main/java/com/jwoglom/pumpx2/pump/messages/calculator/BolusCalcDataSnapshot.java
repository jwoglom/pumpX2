package com.jwoglom.pumpx2.pump.messages.calculator;

import com.google.common.annotations.VisibleForTesting;
import com.jwoglom.pumpx2.pump.messages.models.InsulinUnit;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BolusCalcDataSnapshotResponse;

import java.util.Optional;

class BolusCalcDataSnapshot {
    boolean carbEntryEnabled;
    long carbRatio;
    double iob;
    int cartridgeRemainingInsulin;
    int correctionFactor;
    int isf;
    boolean isAutopopAllowed;
    int targetBg;
    boolean exceeded;
    int maxBolusAmount;
    long maxBolusHourlyTotal;

    BolusCalcDataSnapshot() {}

    BolusCalcDataSnapshot(BolusCalcDataSnapshotResponse resp) {
        this(
            resp.getCarbEntryEnabled(),
            resp.getCarbRatio(),
            resp.getIob(),
            resp.getCartridgeRemainingInsulin(),
            resp.getCorrectionFactor(),
            resp.getIsf(),
            resp.getIsAutopopAllowed(),
            resp.getTargetBg(),
            resp.getMaxBolusEventsExceeded() || resp.getMaxIobEventsExceeded(),
            resp.getMaxBolusAmount(),
            resp.getMaxBolusHourlyTotal());
    }

    BolusCalcDataSnapshot(
        boolean carbEntryEnabled,
        long carbRatio,
        long iob,
        int cartridgeRemainingInsulin,
        int correctionFactor,
        int isf,
        boolean isAutopopAllowed,
        int targetBg,
        boolean exceeded,
        int maxBolusAmount,
        long maxBolusHourlyTotal)
    {
        this.carbEntryEnabled = carbEntryEnabled;
        this.carbRatio = carbRatio;
        this.iob = InsulinUnit.from1000To1(iob); // 1000-unit
        this.cartridgeRemainingInsulin = cartridgeRemainingInsulin;
        this.correctionFactor = correctionFactor; // the current BG
        this.isf = isf;
        this.isAutopopAllowed = isAutopopAllowed;
        this.targetBg = targetBg;
        this.exceeded = exceeded;
        this.maxBolusAmount = maxBolusAmount;
        this.maxBolusHourlyTotal = maxBolusHourlyTotal;
    }

    @VisibleForTesting
    public BolusCalcDataSnapshot withIOB(double newIob) {
        return new BolusCalcDataSnapshot(carbEntryEnabled, carbRatio, InsulinUnit.from1To1000(newIob), cartridgeRemainingInsulin, correctionFactor, isf, isAutopopAllowed, targetBg, exceeded, maxBolusAmount, maxBolusHourlyTotal);
    }

    @VisibleForTesting
    public BolusCalcDataSnapshot withCorrectionFactor(int newCorrectionFactor) {
        return new BolusCalcDataSnapshot(carbEntryEnabled, carbRatio, InsulinUnit.from1To1000(iob), cartridgeRemainingInsulin, newCorrectionFactor, isf, isAutopopAllowed, targetBg, exceeded, maxBolusAmount, maxBolusHourlyTotal);
    }

    @VisibleForTesting
    public BolusCalcDataSnapshotResponse toRawResponse() {
        return new BolusCalcDataSnapshotResponse(false, correctionFactor, InsulinUnit.from1To1000(iob), cartridgeRemainingInsulin, targetBg, isf, carbEntryEnabled, carbRatio, maxBolusAmount, maxBolusHourlyTotal, exceeded, exceeded, isAutopopAllowed);
    }
}