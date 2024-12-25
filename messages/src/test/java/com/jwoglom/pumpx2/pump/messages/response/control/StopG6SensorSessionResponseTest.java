package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class StopG6SensorSessionResponseTest {
    @Test
    public void testStopG6SensorSessionResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        StopG6SensorSessionResponse expected = new StopG6SensorSessionResponse(
            0
        );

        StopG6SensorSessionResponse parsedRes = (StopG6SensorSessionResponse) MessageTester.test(
                "005ab55a19002cc7f21f7fc48fc9d4bfe3feca73675911cca9f1b3a96caba40c",
                90,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}