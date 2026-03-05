package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HighestAamRequestTest {
    @Test
    public void testHighestAamRequest() throws DecoderException {
        // empty cargo
        HighestAamRequest expected = new HighestAamRequest();

        HighestAamRequest parsedReq = (HighestAamRequest) MessageTester.test(
                "0007780700a224",
                7,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
