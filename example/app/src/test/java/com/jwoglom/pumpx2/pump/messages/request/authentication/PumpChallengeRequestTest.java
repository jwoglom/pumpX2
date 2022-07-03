package com.jwoglom.pumpx2.pump.messages.request.authentication;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpChallengeRequestTest {
    @Test
    public void testTconnectAppPumpChallengeRequest() throws DecoderException {
        PumpChallengeRequest expected = new PumpChallengeRequest(1, new byte[]{
            1, -108, -88, -7, -116, -92, -100, -35, -9, 12, 44, 19, 49, 115, 2, -112, -68, -93, -33, 7
        });

        PumpChallengeRequest parsedReq = (PumpChallengeRequest) MessageTester.testMultiplePackets(
                ImmutableList.of(
                        "010112011601000194a8f98ca49cddf70c2c1331",
                        "0001730290bca3df079967"
                ),
                1,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getAppInstanceId(), parsedReq.getAppInstanceId());
        assertHexEquals(expected.getPumpChallengeHash(), parsedReq.getPumpChallengeHash());
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
