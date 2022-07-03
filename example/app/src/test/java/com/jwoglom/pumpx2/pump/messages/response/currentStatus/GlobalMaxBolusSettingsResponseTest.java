package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.GlobalMaxBolusSettingsResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class GlobalMaxBolusSettingsResponseTest {
    @Test
    @Ignore("needs to be run on a V2 pump")
    public void testGlobalMaxBolusSettingsResponse() throws DecoderException {
        GlobalMaxBolusSettingsResponse expected = new GlobalMaxBolusSettingsResponse(
            // int maxBolus, int maxBolusDefault
        );

        GlobalMaxBolusSettingsResponse parsedRes = (GlobalMaxBolusSettingsResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}