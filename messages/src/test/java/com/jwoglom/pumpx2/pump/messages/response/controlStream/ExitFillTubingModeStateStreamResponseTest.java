package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ExitFillTubingModeStateStreamResponseTest {
    @Test
    public void testExitFillTubingModeStateStreamResponse() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        ExitFillTubingModeStateStreamResponse expected = new ExitFillTubingModeStateStreamResponse(
            0
        );

        ExitFillTubingModeStateStreamResponse parsedRes = (ExitFillTubingModeStateStreamResponse) MessageTester.test(
                "0091e9911900aec84020f5be25220634beb8ffff2cad2cf936582ee0226d2f28",
                -111,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}