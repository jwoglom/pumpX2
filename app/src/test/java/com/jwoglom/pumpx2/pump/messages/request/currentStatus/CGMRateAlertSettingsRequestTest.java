package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMRateAlertSettingsRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMRateAlertSettingsRequestTest {
    @Test
    public void testCGMRateAlertSettingsRequest() throws DecoderException {
        // empty cargo
        CGMRateAlertSettingsRequest expected = new CGMRateAlertSettingsRequest();

        CGMRateAlertSettingsRequest parsedReq = (CGMRateAlertSettingsRequest) MessageTester.test(
                "00045c0400f72b",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}