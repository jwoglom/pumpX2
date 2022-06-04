package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQInfoV1ResponseTest {
    @Test
    public void testControlIQInfoResponsePounds() throws DecoderException {
        ControlIQInfoV1Response expected = new ControlIQInfoV1Response(
            // boolean closedLoopEnabled, int weight, int weightUnit, int totalDailyInsulin, int currentUserModeType, int byte6, int byte7, int byte8, int controlStateType
            true, 150, 1, 75, 0, 0, 0, 4, 0
        );

        ControlIQInfoV1Response parsedRes = (ControlIQInfoV1Response) MessageTester.test(
                "000369030a019600014b0000000400b912",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testControlIQInfoResponseKilograms() throws DecoderException {
        ControlIQInfoV1Response expected = new ControlIQInfoV1Response(
                // boolean closedLoopEnabled, int weight, int weightUnit, int totalDailyInsulin, int currentUserModeType, int byte6, int byte7, int byte8, int controlStateType
                true, 140, 2, 75, 0, 0, 0, 4, 0
        );

        ControlIQInfoV1Response parsedRes = (ControlIQInfoV1Response) MessageTester.test(
                "000469040a018c00024b0000000400167e",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}