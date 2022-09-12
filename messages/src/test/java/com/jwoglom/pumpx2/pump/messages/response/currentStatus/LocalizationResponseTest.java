package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class LocalizationResponseTest {
    @Test
    public void testLocalizationResponse() throws DecoderException { 
        LocalizationResponse expected = new LocalizationResponse(
            // int glucoseOUM, int regionSetting, int languageSelected, long languagesAvailableBitmask
                0, 0, 2, 1L
        );

        LocalizationResponse parsedRes = (LocalizationResponse) MessageTester.test(
                "0004a70407000002010000003a49",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}