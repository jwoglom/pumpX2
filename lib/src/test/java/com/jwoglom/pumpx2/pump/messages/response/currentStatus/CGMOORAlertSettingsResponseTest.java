package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMOORAlertSettingsResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMOORAlertSettingsResponseTest {
    @Test
    public void testCGMOORAlertSettingsResponse() throws DecoderException {
        CGMOORAlertSettingsResponse expected = new CGMOORAlertSettingsResponse(
            // int sensorTimeoutAlertEnabled, int sensorTimeoutAlertThreshold, int sensorTimeoutDefaultBitmask
                20, 1, 5
        );

        CGMOORAlertSettingsResponse parsedRes = (CGMOORAlertSettingsResponse) MessageTester.test(
                "00035f0303140105be32",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}