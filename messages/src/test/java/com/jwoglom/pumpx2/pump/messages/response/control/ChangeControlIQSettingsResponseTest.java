package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ChangeControlIQSettingsResponseTest {
    @Test
    public void testChangeControlIQSettingsResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        ChangeControlIQSettingsResponse expected = new ChangeControlIQSettingsResponse(
            new byte[3]
        );

        ChangeControlIQSettingsResponse parsedRes = (ChangeControlIQSettingsResponse) MessageTester.test(
                "005ccb5c1b000000d9a3ee1f799d9f28e8a6759fed2cb0fafeffdea01a2b0cf56bb0",
                92,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}