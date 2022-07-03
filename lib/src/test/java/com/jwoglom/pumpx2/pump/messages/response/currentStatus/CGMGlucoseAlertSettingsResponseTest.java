package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMGlucoseAlertSettingsResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMGlucoseAlertSettingsResponseTest {
    @Test
    public void testCGMGlucoseAlertSettingsResponse1() throws DecoderException {
        CGMGlucoseAlertSettingsResponse expected = new CGMGlucoseAlertSettingsResponse(
                // High Alert 200mg/dl, 1 hour
                // Low Alert 80mg/dl, 30 min
                200, 1, 60, 5,
                80, 1, 30, 5
        );

        CGMGlucoseAlertSettingsResponse parsedRes = (CGMGlucoseAlertSettingsResponse) MessageTester.test(
                "00035b030cc800013c00055000011e00052a58",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCGMGlucoseAlertSettingsResponse2() throws DecoderException {
        CGMGlucoseAlertSettingsResponse expected = new CGMGlucoseAlertSettingsResponse(
                // High Alert disabled (prev 200mg/dl, 1 hour)
                // Low Alert 80mg/dl, 30 min
                200, 0, 60, 1,
                80, 1, 30, 5
        );

        CGMGlucoseAlertSettingsResponse parsedRes = (CGMGlucoseAlertSettingsResponse) MessageTester.test(
                "00045b040cc800003c00015000011e0005af86",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCGMGlucoseAlertSettingsResponse3() throws DecoderException {
        CGMGlucoseAlertSettingsResponse expected = new CGMGlucoseAlertSettingsResponse(
                // High Alert 200mg/dl, Never repeat
                // Low Alert 80mg/dl, 30 min
                200, 1, 0, 1,
                80, 1, 30, 5
        );

        CGMGlucoseAlertSettingsResponse parsedRes = (CGMGlucoseAlertSettingsResponse) MessageTester.test(
                "00055b050cc800010000015000011e000599d3",
                5,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCGMGlucoseAlertSettingsResponse4() throws DecoderException {
        CGMGlucoseAlertSettingsResponse expected = new CGMGlucoseAlertSettingsResponse(
                // High Alert 250mg/dl, 5 hr
                // Low Alert 80mg/dl, 30 min
                250, 1, 300, 0,
                80, 1, 30, 5
        );

        CGMGlucoseAlertSettingsResponse parsedRes = (CGMGlucoseAlertSettingsResponse) MessageTester.test(
                "00065b060cfa00012c01005000011e00057eec",
                6,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCGMGlucoseAlertSettingsResponse5() throws DecoderException {
        CGMGlucoseAlertSettingsResponse expected = new CGMGlucoseAlertSettingsResponse(
                // High Alert 120mg/dl, 5 hr
                // Low Alert 80mg/dl, 30 min
                120, 1, 300, 0,
                80, 1, 30, 5
        );

        CGMGlucoseAlertSettingsResponse parsedRes = (CGMGlucoseAlertSettingsResponse) MessageTester.test(
                "00075b070c7800012c01005000011e0005543b",
                7,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}