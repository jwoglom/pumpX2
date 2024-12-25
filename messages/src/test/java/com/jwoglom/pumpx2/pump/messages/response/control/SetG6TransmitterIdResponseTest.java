package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetG6TransmitterIdResponseTest {
    @Test
    public void testSetG6TransmitterIdResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        SetG6TransmitterIdResponse expected = new SetG6TransmitterIdResponse(
            // 
        );

        SetG6TransmitterIdResponse parsedRes = (SetG6TransmitterIdResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}