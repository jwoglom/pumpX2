package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMRateAlertSettingsResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMRateAlertSettingsResponseTest {
    @Test
    public void testCGMRateAlertSettingsResponse() throws DecoderException {
        CGMRateAlertSettingsResponse expected = new CGMRateAlertSettingsResponse(
            // int riseRateThreshold, int riseRateEnabled, int riseRateDefaultBitmask, int fallRateThreshold, int fallRateEnabled, int fallRateDefaultBitmask
                3, 1, 1, 3, 1, 1
        );

        CGMRateAlertSettingsResponse parsedRes = (CGMRateAlertSettingsResponse) MessageTester.test(
                "00045d04060301010301016b8c",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCGMRateAlertSettingsResponse2() throws DecoderException {
        CGMRateAlertSettingsResponse expected = new CGMRateAlertSettingsResponse(
                // int riseRateThreshold, int riseRateEnabled, int riseRateDefaultBitmask, int fallRateThreshold, int fallRateEnabled, int fallRateDefaultBitmask
                3, 1, 1, 2, 1, 0
        );

        CGMRateAlertSettingsResponse parsedRes = (CGMRateAlertSettingsResponse) MessageTester.test(
                "00055d0506030101020100a9ec",
                5,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}