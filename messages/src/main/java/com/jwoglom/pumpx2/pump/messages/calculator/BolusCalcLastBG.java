package com.jwoglom.pumpx2.pump.messages.calculator;

import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BolusCalcDataSnapshotResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBGResponse;

import org.apache.commons.lang3.tuple.Pair;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BolusCalcLastBG {
    private final BolusCalcDataSnapshotResponse dataSnapshot;
    private final LastBGResponse lastBGResponse;


    BolusCalcLastBG(BolusCalcDataSnapshotResponse dataSnapshot, LastBGResponse lastBGResponse) {
        this.dataSnapshot = dataSnapshot;
        this.lastBGResponse = lastBGResponse;
    }

    public Pair<Integer, List<BolusCalcCondition>> determineLastBG() {
        Integer bgValue = null;

        // Correction factor from BolusCalcDataSnapshot, aka the most recent CGM reading if it qualifies
        if (dataSnapshot != null) {
            if (dataSnapshot.getCorrectionFactor() > 0 && dataSnapshot.getIsAutopopAllowed()) {
                bgValue = dataSnapshot.getCorrectionFactor();
            }
        }

        // If the data from LastBGResponse is empty, then we rely on the correctionFactor.
        if (lastBGResponse == null || lastBGResponse.getBgValue() == 0) {
            if (bgValue == null) {
                return Pair.of(null, ImmutableList.of(new BolusCalcCondition.DataDecision("not filling BG", "not present in data snapshot or LastBGResponse")));
            } else {
                return Pair.of(bgValue, ImmutableList.of(new BolusCalcCondition.DataDecision("filling BG from CGM")));
            }
        }

        long durationMins = ChronoUnit.MINUTES.between(lastBGResponse.getBgTimestampInstant(), Instant.now());
        if (bgValue == null && durationMins >= 15) {
            return Pair.of(null, ImmutableList.of(new BolusCalcCondition.DataDecision("not filling BG", "no BG in data snapshot and LastBGResponse BG is too old")));
        }

        if (bgValue == null) {
            if (lastBGResponse.getBgValue() >= 40 && lastBGResponse.getBgValue() <= 400) {
                return Pair.of(lastBGResponse.getBgValue(), ImmutableList.of(new BolusCalcCondition.DataDecision("filling BG from " + lastBGResponse.getBgSource().name())));
            } else {
                return Pair.of(null, ImmutableList.of(new BolusCalcCondition.FailedSanityCheck("LastBGResponse BG not within interval")));
            }
        }

        // Default to using BolusCalcDataSnapshot correctionFactor if it exists
        return Pair.of(bgValue, ImmutableList.of(new BolusCalcCondition.DataDecision("filling BG from CGM")));
    }
}
