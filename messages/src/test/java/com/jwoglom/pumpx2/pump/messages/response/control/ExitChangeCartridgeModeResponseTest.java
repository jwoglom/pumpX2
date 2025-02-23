package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ExitChangeCartridgeModeResponseTest {
    @Test
    public void testExitChangeCartridgeModeResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        ExitChangeCartridgeModeResponse expected = new ExitChangeCartridgeModeResponse(
            // int status
        );

        ExitChangeCartridgeModeResponse parsedRes = (ExitChangeCartridgeModeResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}