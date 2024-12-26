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
            0
        );

        SetG6TransmitterIdResponse parsedRes = (SetG6TransmitterIdResponse) MessageTester.test(
                "0021b121190085c3f21f51e3fabfa0a59cd348aed259b44201903b33d9bead20",
                33,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}