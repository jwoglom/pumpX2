package com.jwoglom.pumpx2.pump.messages.builders;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeRequest;

import java.nio.charset.Charset;

public class PumpChallengeBuilder {
    public static PumpChallengeRequest create(int appInstanceId, String pairingCode, byte[] hmacKey) {
        String pairingChars = processPairingCode(pairingCode);
        Preconditions.checkArgument(pairingChars.length() == 16);
        return new PumpChallengeRequest(
                appInstanceId,
                Packetize.doHmacSha1(hmacKey, pairingChars.getBytes(Charset.forName("UTF-8"))));
    }

    // Remove all dashes and spaces
    private static String processPairingCode(String pairingCode) {
        String processed = "";
        for (Character c : pairingCode.toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                processed += c;
            }
        }
        return processed;
    }
}
