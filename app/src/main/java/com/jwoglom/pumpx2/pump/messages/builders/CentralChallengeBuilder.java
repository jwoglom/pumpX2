package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.authentication.CentralChallengeRequest;

public class CentralChallengeBuilder {
    public static CentralChallengeRequest create(int appInstanceId) {
        byte[] centralChallenge = Bytes.getSecureRandom10Bytes();
        return new CentralChallengeRequest(appInstanceId, centralChallenge);
    }
}
