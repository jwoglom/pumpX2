package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.models.PairingCodeType;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.AbstractCentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeV2Response;

import java.nio.charset.Charset;

public class PumpChallengeRequestBuilder {
    public static PumpChallengeRequest create(AbstractCentralChallengeResponse challengeResponse, String pairingCode) throws InvalidPairingCodeFormat {
        if (challengeResponse instanceof CentralChallengeResponse) {
            return createV1((CentralChallengeResponse) challengeResponse, pairingCode);
        } else if (challengeResponse instanceof CentralChallengeV2Response) {
            return createV2((CentralChallengeV2Response) challengeResponse, pairingCode);
        } else {
            throw new RuntimeException("invalid CentralChallengeResponse");
        }
    }

    public static String processPairingCode(String pairingCode, PairingCodeType type) throws InvalidPairingCodeFormat {
        if (type == PairingCodeType.LONG_16CHAR) {
            String processed = type.filterCharacters(pairingCode);
            if (processed.length() != 16) {
                throw new InvalidLongPairingCodeFormat();
            }
            return processed;
        } else if (type == PairingCodeType.SHORT_6CHAR) {
            String processed = type.filterCharacters(pairingCode);
            if (processed.length() != 6) {
                throw new InvalidShortPairingCodeFormat();
            }
            return processed;
        } else {
            throw new InvalidPairingCodeFormat("invalid PairingCodeType");
        }
    }

    public static String processPairingCode(String pairingCode) throws InvalidPairingCodeFormat {
        if (pairingCode.length() == 6 || PairingCodeType.SHORT_6CHAR.filterCharacters(pairingCode).length() == 6) {
            return processPairingCode(pairingCode, PairingCodeType.SHORT_6CHAR);
        }
        return processPairingCode(pairingCode, PairingCodeType.LONG_16CHAR);
    }

    // HMAC sha1
    public static PumpChallengeRequest createV1(CentralChallengeResponse challengeResponse, String pairingCode) throws InvalidPairingCodeFormat {
        int appInstanceId = challengeResponse.getAppInstanceId();
        byte[] hmacKey = challengeResponse.getHmacKey();

        String pairingChars = processPairingCode(pairingCode, PairingCodeType.LONG_16CHAR);
        byte[] challengeHash = Packetize.doHmacSha1(hmacKey, pairingChars.getBytes(Charset.forName("UTF-8")));
        return new PumpChallengeRequest(
                appInstanceId,
                challengeHash);
    }

    // ECJPake (Password Authenticated Key Exchange by Juggling over Eliptic Curve)
    public static PumpChallengeRequest createV2(CentralChallengeV2Response challengeResponse, String pairingCode) throws InvalidPairingCodeFormat {
        // TODO
        String pairingChars = processPairingCode(pairingCode, PairingCodeType.SHORT_6CHAR);
        return null;
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
