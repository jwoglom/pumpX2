package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.authentication.CentralChallengeRequest;

public class CentralChallengeBuilder {
    public static CentralChallengeRequest create(int appInstanceId) {
        // TODO: this should be 8 instead of 10, we cut off 2 bytes at the end for no reason
        byte[] centralChallenge = Bytes.getSecureRandom10Bytes();
        return new CentralChallengeRequest(appInstanceId, centralChallenge);
    }
}
