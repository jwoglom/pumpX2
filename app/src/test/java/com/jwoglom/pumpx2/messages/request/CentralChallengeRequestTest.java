package com.jwoglom.pumpx2.messages.request;

import static com.jwoglom.pumpx2.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.CentralChallengeRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CentralChallengeRequestTest {
    @Test
    public void testTconnectAppFirstRequest() throws DecoderException {
        CentralChallengeRequest expected = new CentralChallengeRequest(0, new byte[]{77, 8, 67, 93, -94, 105, 71, 53});

        CentralChallengeRequest parsedReq = (CentralChallengeRequest) MessageTester.test(
                "000010000a00004d08435da26947356d6f",
                0,
                1,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getAppInstanceId(), parsedReq.getAppInstanceId());
        assertHexEquals(expected.getCentralChallenge(), parsedReq.getCentralChallenge());
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
