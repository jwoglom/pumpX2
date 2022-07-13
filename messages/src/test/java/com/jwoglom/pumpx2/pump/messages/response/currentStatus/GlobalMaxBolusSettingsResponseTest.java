package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class GlobalMaxBolusSettingsResponseTest {
    @Test
    public void testGlobalMaxBolusSettingsResponse() throws DecoderException {
        GlobalMaxBolusSettingsResponse expected = new GlobalMaxBolusSettingsResponse(
            // int maxBolus, int maxBolusDefault
            25000, 10000
        );

        GlobalMaxBolusSettingsResponse parsedRes = (GlobalMaxBolusSettingsResponse) MessageTester.test(
                "00188d1804a8611027e5ba",
                24,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}