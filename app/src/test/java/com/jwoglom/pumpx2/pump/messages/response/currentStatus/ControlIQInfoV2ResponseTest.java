package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQInfoV2Response;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class ControlIQInfoV2ResponseTest {
    @Test
    @Ignore("needs to be run on a V2 pump")
    public void testControlIQInfoV2Response() throws DecoderException {
        ControlIQInfoV2Response expected = new ControlIQInfoV2Response(
            // boolean closedLoopEnabled, int weight, int weightUnit, int totalDailyInsulin, int currentUserModeType, int byte6, int byte7, int byte8, int controlStateType, int exerciseChoice, int exerciseDuration
        );

        ControlIQInfoV2Response parsedRes = (ControlIQInfoV2Response) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}