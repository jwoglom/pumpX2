package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSettingsResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class IDPSettingsResponseTest {
    @Test
    public void testIDPSettingsResponse() throws DecoderException {
        IDPSettingsResponse expected = new IDPSettingsResponse(
            // int idp, int name, int tDependentNum, int insulinDuration, int maxBolus, boolean carbEntry
            0, "A", 4, 300, 25000, true
        );

        IDPSettingsResponse parsedRes = (IDPSettingsResponse) MessageTester.test(
                "00044104170041000000000000000000000000000000042c01a86101c2a5",
                4,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


    @Test
    public void testIDPSettingsResponse2() throws DecoderException {
        // insulin duration: 8 hrs
        // carbs: on
        // name: "Na"
        // max bolus: 25u
        IDPSettingsResponse expected = new IDPSettingsResponse(
                // int idp, int name, int tDependentNum, int insulinDuration, int maxBolus, boolean carbEntry
                1, "Na", 1, 480, 25000, true
        );

        IDPSettingsResponse parsedRes = (IDPSettingsResponse) MessageTester.test(
                "0005410517014e61000000000000000000000000000001e001a861015ef2",
                5,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }


}