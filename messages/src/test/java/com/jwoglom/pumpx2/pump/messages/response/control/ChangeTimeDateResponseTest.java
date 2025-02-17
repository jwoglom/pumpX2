package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ChangeTimeDateResponseTest {
    @Test
    public void testChangeTimeDateResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        ChangeTimeDateResponse expected = new ChangeTimeDateResponse(
            // int status
            0
        );

        ChangeTimeDateResponse parsedRes = (ChangeTimeDateResponse) MessageTester.test(
                "0043d743190030173920f8632cafa89dbda3879449357f9242636693d60cf4fb",
                67,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}