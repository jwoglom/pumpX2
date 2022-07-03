package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMAlertStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMAlertStatusRequestTest {
    @Test
    public void testCGMAlertStatusRequest() throws DecoderException {
        // empty cargo
        CGMAlertStatusRequest expected = new CGMAlertStatusRequest();

        CGMAlertStatusRequest parsedReq = (CGMAlertStatusRequest) MessageTester.test(
                "00034a0300a343",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}