package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class ControlIQInfoV2ResponseTest {
    /** v2.5 API */
    @Test
    public void testControlIQInfoV2Response() throws DecoderException {
        ControlIQInfoV2Response expected = new ControlIQInfoV2Response(
            // boolean closedLoopEnabled, int weight, int weightUnit, int totalDailyInsulin, int currentUserModeType, int byte6, int byte7, int byte8, int controlStateType, int exerciseChoice, int exerciseDuration
                true, 150, 1, 75, 1, 0, 1, 1, 3, 0, 0
        );

        ControlIQInfoV2Response parsedRes = (ControlIQInfoV2Response) MessageTester.test(
                "000cb30c13019600014b01000101030000000000000000002d45",
                12,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}