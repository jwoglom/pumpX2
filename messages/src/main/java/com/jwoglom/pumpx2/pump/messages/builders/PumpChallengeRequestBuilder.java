package com.jwoglom.pumpx2.pump.messages.builders;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.models.PairingCodeType;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeRequest;

import java.nio.charset.Charset;

public class PumpChallengeRequestBuilder {
    public static PumpChallengeRequest create(int appInstanceId, String pairingCode, byte[] hmacKey) throws InvalidPairingCodeFormat {
        PairingCodeType type = PairingCodeType.LONG_16CHAR;
        if (pairingCode.length() == 6) {
            type = PairingCodeType.SHORT_6CHAR;
        }
        return create(appInstanceId, pairingCode, type, hmacKey);
    }

    public static PumpChallengeRequest create(int appInstanceId, String pairingCode, PairingCodeType type, byte[] hmacKey) throws InvalidPairingCodeFormat {
        String pairingChars = processPairingCode(pairingCode, type);
        return new PumpChallengeRequest(
                appInstanceId,
                Packetize.doHmacSha1(hmacKey, pairingChars.getBytes(Charset.forName("UTF-8"))));
    }

    public static String processPairingCode(String pairingCode, PairingCodeType type) throws InvalidPairingCodeFormat {
        if (type == PairingCodeType.LONG_16CHAR) {
            // Remove all dashes and spaces
            String processed = "";
            for (Character c : pairingCode.toCharArray()) {
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                    processed += c;
                }
            }
            if (processed.length() != 16) {
                throw new InvalidLongPairingCodeFormat();
            }
            return processed;
        } else if (type == PairingCodeType.SHORT_6CHAR) {
            // Remove all non-numbers and spaces
            String processed = "";
            for (Character c : pairingCode.toCharArray()) {
                if ((c >= '0' && c <= '9')) {
                    processed += c;
                }
            }
            if (processed.length() != 6) {
                throw new InvalidShortPairingCodeFormat();
            }
            return processed;
        } else {
            throw new InvalidPairingCodeFormat("");
        }
    }

    public static class InvalidPairingCodeFormat extends Exception {
        InvalidPairingCodeFormat(String reason) {
            super("The pairing code entered does not match the expected format. " + reason);
        }
    }

    public static class InvalidLongPairingCodeFormat extends InvalidPairingCodeFormat {
        InvalidLongPairingCodeFormat() {
            super("It should be 16 alphanumeric characters total across 5 groups of 4 characters each.");
        }
    }
    public static class InvalidShortPairingCodeFormat extends InvalidPairingCodeFormat {
        InvalidShortPairingCodeFormat() {
            super("It should be 6 numbers.");
        }
    }
}
