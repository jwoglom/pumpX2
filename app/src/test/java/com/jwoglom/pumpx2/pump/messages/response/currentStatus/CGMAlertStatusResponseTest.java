package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMAlertStatusResponseTest {
    @Test
    public void testCGMAlertStatusResponse() throws DecoderException {
        CGMAlertStatusResponse expected = new CGMAlertStatusResponse(
            // BigInteger cgmAlertBitmask
            0L
        );

        CGMAlertStatusResponse parsedRes = (CGMAlertStatusResponse) MessageTester.test(
                "00034b030800000000000000009ed2",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}