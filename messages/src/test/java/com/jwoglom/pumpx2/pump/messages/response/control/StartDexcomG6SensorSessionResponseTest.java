package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class StartDexcomG6SensorSessionResponseTest {
    @Test
    public void testStartG6SensorSessionResponse() throws DecoderException { 
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);
        
        StartDexcomG6SensorSessionResponse expected = new StartDexcomG6SensorSessionResponse(
            0
        );

        StartDexcomG6SensorSessionResponse parsedRes = (StartDexcomG6SensorSessionResponse) MessageTester.test(
                "002eb32e1900cbc3f21f0a67a11d39b6d34d4936e72c6cfb468c9a4b768c6115",
                46,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}