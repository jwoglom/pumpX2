package com.jwoglom.pumpx2.pump.messages.response;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.PumpSettingsResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpSettingsResponseTest {
    @Test
    public void testPumpSettingsResponse() throws DecoderException {
        PumpSettingsResponse expected = new PumpSettingsResponse(
            // int lowInsulinThreshold, int cannulaPrimeSize, int autoShutdownEnabled, int autoShutdownDuration, int featureLock, int oledTimeout, int status
                35, 30, 1, 18, 0, 120, 93
        );

        PumpSettingsResponse parsedRes = (PumpSettingsResponse) MessageTester.test(
                "0003530309231e01120000785d00703f",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}