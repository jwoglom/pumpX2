package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class ControlIQInfoV2RequestTest {
    @Test
    @Ignore("pump api ver v2.1 gives unexpected opcode")
    public void testControlIQInfoV2Request() throws DecoderException {
        // empty cargo
        ControlIQInfoV2Request expected = new ControlIQInfoV2Request();

        ControlIQInfoV2Request parsedReq = (ControlIQInfoV2Request) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}