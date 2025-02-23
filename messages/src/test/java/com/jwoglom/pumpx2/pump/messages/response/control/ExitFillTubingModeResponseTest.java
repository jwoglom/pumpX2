package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ExitFillTubingModeResponseTest {
    @Test
    public void testExitFillTubingModeResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        ExitFillTubingModeResponse expected = new ExitFillTubingModeResponse(
            0
        );

        ExitFillTubingModeResponse parsedRes = (ExitFillTubingModeResponse) MessageTester.test(
                "009197911900aec8402021f71d880600f40c8f92f96bbca87dbea12d73ad213b",
                -111,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}