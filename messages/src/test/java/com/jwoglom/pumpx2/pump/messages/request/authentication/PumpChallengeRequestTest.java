package com.jwoglom.pumpx2.pump.messages.request.authentication;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.junit.Test;

public class PumpChallengeRequestTest {
    @Test
    public void testTconnectAppPumpChallengeRequest() {
        PumpChallengeRequest expected = new PumpChallengeRequest(1, new byte[]{
            1, -108, -88, -7, -116, -92, -100, -35, -9, 12, 44, 19, 49, 115, 2, -112, -68, -93, -33, 7
        });

        PumpChallengeRequest parsedReq = (PumpChallengeRequest) MessageTester.testMultiplePackets(
                Arrays.asList(
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
