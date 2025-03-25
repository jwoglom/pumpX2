package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetPumpSoundsResponseTest {
    @Test
    public void testSetPumpSoundsResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        SetPumpSoundsResponse expected = new SetPumpSoundsResponse(
            0
        );

        SetPumpSoundsResponse parsedRes = (SetPumpSoundsResponse) MessageTester.test(
                "004ee54e1900648168208bb1895bc466198b6c049eb08f073d9afed28dba8c00",
                78,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}