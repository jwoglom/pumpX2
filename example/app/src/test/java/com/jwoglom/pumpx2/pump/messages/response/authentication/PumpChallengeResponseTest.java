package com.jwoglom.pumpx2.pump.messages.response.authentication;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpChallengeResponseTest {
    @Test
    public void testTconnectAppChallengeResponseMessageSuccess() throws DecoderException {
        PumpChallengeResponse expected = new PumpChallengeResponse(1, true);

        PumpChallengeResponse parsedRes = (PumpChallengeResponse) MessageTester.test(
                "0001130103010001e8cc",
                1,
                1,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getAppInstanceId(), parsedRes.getAppInstanceId());
        assertEquals(expected.getSuccess(), parsedRes.getSuccess());
    }

    @Test
    public void testTconnectAppChallengeResponseMessageFailure() throws DecoderException {
        PumpChallengeResponse expected = new PumpChallengeResponse(1, false);

        PumpChallengeResponse parsedRes = (PumpChallengeResponse) MessageTester.test(
                "0001130103010000c9dc",
                1,
                1,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getAppInstanceId(), parsedRes.getAppInstanceId());
        assertEquals(expected.getSuccess(), parsedRes.getSuccess());
    }
}
