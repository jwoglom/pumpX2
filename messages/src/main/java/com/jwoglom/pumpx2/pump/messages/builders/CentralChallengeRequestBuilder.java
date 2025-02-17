package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.PairingCodeType;
import com.jwoglom.pumpx2.pump.messages.request.authentication.CentralChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.AbstractCentralChallengeRequest;

public class CentralChallengeRequestBuilder {
    public static AbstractCentralChallengeRequest create(int appInstanceId) {
        if (PumpStateSupplier.pumpApiVersion.get().greaterThan(KnownApiVersion.API_V3)) {
            return null; // Handled inside pair()
        } else {
            return createV1(appInstanceId);
        }
    }

    private static AbstractCentralChallengeRequest createV1(int appInstanceId) {
        // TODO: this should be 8 instead of 10, we cut off 2 bytes at the end for no reason
        byte[] centralChallenge = Bytes.getSecureRandom10Bytes();
        return new CentralChallengeRequest(appInstanceId, centralChallenge);
    }
}
