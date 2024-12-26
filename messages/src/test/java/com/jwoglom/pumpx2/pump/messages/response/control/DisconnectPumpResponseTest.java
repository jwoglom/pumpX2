package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DisconnectPumpResponseTest {
    @Test
    public void testDisconnectPumpResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        DisconnectPumpResponse expected = new DisconnectPumpResponse(
            0
        );

        DisconnectPumpResponse parsedRes = (DisconnectPumpResponse) MessageTester.test(
                "00debfde1900000000003fad35823cf021e2dad26d3f485cd5b4557dfe88ef21",
                -34,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}