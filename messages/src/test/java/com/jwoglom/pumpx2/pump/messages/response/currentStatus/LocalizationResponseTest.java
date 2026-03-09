package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class LocalizationResponseTest {
    @Test
    public void testLocalizationResponse_X2() throws DecoderException {
        LocalizationResponse expected = new LocalizationResponse(
            // int glucoseUOM, int languageSelected, int regionSetting, long languagesAvailableBitmask
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
        assertEquals(0, parsedRes.getGlucoseUOM());
        assertEquals(0, parsedRes.getLanguageSelected());
        assertEquals(2, parsedRes.getRegionSetting());
        assertEquals(1L, parsedRes.getLanguagesAvailableBitmask());
    }


    @Test
    public void testLocalizationResponse_Mobi() throws DecoderException {
        LocalizationResponse expected = new LocalizationResponse(
                // int glucoseUOM, int languageSelected, int regionSetting, long languagesAvailableBitmask
                0, 0, 2, 0L
        );

        LocalizationResponse parsedRes = (LocalizationResponse) MessageTester.test(
                "0035a7350700000200000000af20",
                53,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getGlucoseUOM());
        assertEquals(0, parsedRes.getLanguageSelected());
        assertEquals(2, parsedRes.getRegionSetting());
        assertEquals(0L, parsedRes.getLanguagesAvailableBitmask());
    }
}
