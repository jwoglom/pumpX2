package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class MalfunctionStatusRequestTest {
    @Test
    public void testMalfunction2StatusRequest() throws DecoderException {
        // empty cargo
        MalfunctionStatusRequest expected = new MalfunctionStatusRequest();

        MalfunctionStatusRequest parsedReq = (MalfunctionStatusRequest) MessageTester.test(
                "0007780700a224",
                7,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}