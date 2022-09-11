package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class PumpingStateStreamResponseTest {
    @Test
    @Ignore("WIP")
    public void testPumpingStateStreamResponse() throws DecoderException { 
        PumpingStateStreamResponse expected = new PumpingStateStreamResponse(
            // boolean isPumpingStateSetAfterStartUp, long stateBitmask
        );

        PumpingStateStreamResponse parsedRes = (PumpingStateStreamResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}