package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class StartG6SensorSessionResponseTest {
    @Test
    public void testStartG6SensorSessionResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        StartG6SensorSessionResponse expected = new StartG6SensorSessionResponse(
            // int status
        );

        StartG6SensorSessionResponse parsedRes = (StartG6SensorSessionResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}