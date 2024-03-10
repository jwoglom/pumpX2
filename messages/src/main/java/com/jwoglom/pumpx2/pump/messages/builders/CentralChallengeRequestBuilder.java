package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.request.authentication.CentralChallengeV2Request;
import com.jwoglom.pumpx2.pump.messages.request.authentication.AbstractChallengeRequest;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;

public class CentralChallengeRequestBuilder {
    public static AbstractChallengeRequest create(int appInstanceId) {
        // TESTING: using V2 always
        try {
            byte[] centralChallenge = Hex.decodeHex("4104e6acd57cf25de99d99b0552a7a47f68f294a4728d6f0f30322fff4ab1c047efadaa6e9980c28a13cc4eb87064e891916ed27a4881f5d819a3a6c9f55ce4cb08041040fd63cf9c8962e342bac550585ce91b7412b2fcb301bdd28bc2a7625a7961bf1c8c19841fd091e92029e9ba785c7224a183c398e336bb11f36bcec71e83d958d20404e815aca591c128e9bb49751ec080d3e4bd73fdf63d5a106577aaa66d3a79f");
            return new CentralChallengeV2Request(appInstanceId, centralChallenge);
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }

        /*
        // TODO: this should be 8 instead of 10, we cut off 2 bytes at the end for no reason
        byte[] centralChallenge = Bytes.getSecureRandom10Bytes();
        return new CentralChallengeRequest(appInstanceId, centralChallenge);
         */
    }
}
