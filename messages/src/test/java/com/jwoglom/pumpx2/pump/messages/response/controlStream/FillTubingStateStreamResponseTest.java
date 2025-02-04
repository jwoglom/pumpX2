package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class FillTubingStateStreamResponseTest {
    @Test
    public void testControlStreamNeg27Response() throws DecoderException { 
        FillTubingStateStreamResponse expected = new FillTubingStateStreamResponse(
            // int unknown
        );

        FillTubingStateStreamResponse parsedRes = (FillTubingStateStreamResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}