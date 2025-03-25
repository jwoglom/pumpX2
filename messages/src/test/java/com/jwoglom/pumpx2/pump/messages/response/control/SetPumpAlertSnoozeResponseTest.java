package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetPumpAlertSnoozeResponseTest {
    @Test
    public void testSetPumpAlertSnoozeResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        SetPumpAlertSnoozeResponse expected = new SetPumpAlertSnoozeResponse(
            0
        );

        SetPumpAlertSnoozeResponse parsedRes = (SetPumpAlertSnoozeResponse) MessageTester.test(
                "0031d5311900ba80682053558873893a6be84fccd0aa1a79beb7a1eb118a9cb8",
                49,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}