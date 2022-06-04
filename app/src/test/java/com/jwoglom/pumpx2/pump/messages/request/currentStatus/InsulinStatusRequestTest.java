package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.InsulinStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class InsulinStatusRequestTest {
    @Test
    public void testInsulinStatusRequest() throws DecoderException {
        // empty cargo
        InsulinStatusRequest expected = new InsulinStatusRequest();

        InsulinStatusRequest parsedReq = (InsulinStatusRequest) MessageTester.test(
                "00042404005e5a",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}