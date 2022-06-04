package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMGlucoseAlertSettingsRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMGlucoseAlertSettingsRequestTest {
    @Test
    public void testCGMGlucoseAlertSettingsRequest() throws DecoderException {
        // empty cargo
        CGMGlucoseAlertSettingsRequest expected = new CGMGlucoseAlertSettingsRequest();

        CGMGlucoseAlertSettingsRequest parsedReq = (CGMGlucoseAlertSettingsRequest) MessageTester.test(
                "00035a0300c000",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}