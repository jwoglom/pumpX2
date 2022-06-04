package com.jwoglom.pumpx2.pump.messages.request;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.CGMStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMStatusRequestTest {
    @Test
    public void testCGMStatusRequest() throws DecoderException {
        // empty cargo
        CGMStatusRequest expected = new CGMStatusRequest();

        CGMStatusRequest parsedReq = (CGMStatusRequest) MessageTester.test(
                "000350030001c7",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}