package com.jwoglom.pumpx2.pump.messages.builders;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeRequest;

import java.nio.charset.Charset;

public class PumpChallengeRequestBuilder {
    public static PumpChallengeRequest create(int appInstanceId, String pairingCode, byte[] hmacKey) throws InvalidPairingCodeFormat {
        String pairingChars = processPairingCode(pairingCode);
        if(pairingChars.length() != 16) {
            throw new InvalidPairingCodeFormat();
        }
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

    public static class InvalidPairingCodeFormat extends Exception {
        InvalidPairingCodeFormat() {
            super("The pairing code entered does not match the expected format. It should be 16 alphanumeric characters total across 5 groups of 4 characters each.");
        }
    }
}
