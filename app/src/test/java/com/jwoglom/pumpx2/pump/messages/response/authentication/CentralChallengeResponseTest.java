package com.jwoglom.pumpx2.pump.messages.response.authentication;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CentralChallengeResponseTest {
    @Test
    public void testTconnectAppFirstPumpReplyMessage() throws DecoderException {
        CentralChallengeResponse expected = new CentralChallengeResponse(1, new byte[]{
                -116, 33, 45, 122, -113, -67, -88, 95, -125, -93, 68, 2, 84, 72, -115, -5, 86, 18, 100, -20
        }, new byte[]{
                -124, 12, 78, 22, -121, 48, 70, -68
        });

        CentralChallengeResponse parsedRes = (CentralChallengeResponse) MessageTester.test(
                "000011001e01008c212d7a8fbda85f83a3440254488dfb561264ec840c4e16873046bc2c1a",
                0,
                2,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getByte0short(), parsedRes.getByte0short());
        assertHexEquals(expected.getBytes2to22(), parsedRes.getBytes2to22());
        assertHexEquals(expected.getBytes22to30(), parsedRes.getBytes22to30());
    }
}
