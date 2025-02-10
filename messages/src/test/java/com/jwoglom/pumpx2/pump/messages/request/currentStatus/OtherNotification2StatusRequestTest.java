package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class OtherNotification2StatusRequestTest {
    @Test
    public void testMalfunctionStatusRequest() throws DecoderException {
        // empty cargo
        OtherNotification2StatusRequest expected = new OtherNotification2StatusRequest();

        OtherNotification2StatusRequest parsedReq = (OtherNotification2StatusRequest) MessageTester.test(
                "0004760400f06a",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}