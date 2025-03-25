package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

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


    @Test
    public void testPumpSettingsResponse_Mobi() throws DecoderException {
        PumpSettingsResponse expected = new PumpSettingsResponse(
                // int lowInsulinThreshold, int cannulaPrimeSize, int autoShutdownEnabled, int autoShutdownDuration, int featureLock, int oledTimeout, int status
                16, 10, 0, 5, 0, 30, 15
        );

        PumpSettingsResponse parsedRes = new PumpSettingsResponse(new byte[]{16, 10, 0, 5, 0, 0, 30, 15, 0});

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}