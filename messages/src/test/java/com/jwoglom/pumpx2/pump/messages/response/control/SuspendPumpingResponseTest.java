package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class SuspendPumpingResponseTest {
    @Test
    @Ignore("needs full-control pump")
    public void testSuspendPumpingResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        SuspendPumpingResponse expected = new SuspendPumpingResponse(
            // int status
        );

        SuspendPumpingResponse parsedRes = (SuspendPumpingResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}