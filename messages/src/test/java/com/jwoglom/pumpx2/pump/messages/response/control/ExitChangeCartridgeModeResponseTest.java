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
            0
        );

        ExitChangeCartridgeModeResponse parsedRes = (ExitChangeCartridgeModeResponse) MessageTester.test(
                "00899389190078c840201a521d880f1dfa4fd153a975571060d9098181614321",
                -119,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}