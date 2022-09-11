package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class LocalizationResponseTest {
    @Test
    public void testLocalizationResponse() throws DecoderException { 
        LocalizationResponse expected = new LocalizationResponse(
            // int glucoseOUM, int regionSetting, int languageSelected, long languagesAvailableBitmask
        );

        LocalizationResponse parsedRes = (LocalizationResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}