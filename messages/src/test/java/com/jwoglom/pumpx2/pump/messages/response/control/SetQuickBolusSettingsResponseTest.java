package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetQuickBolusSettingsResponseTest {
    @Test
    public void testSetQuickBolusSettingsResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        SetQuickBolusSettingsResponse expected = new SetQuickBolusSettingsResponse(
            new byte[]{0}
        );

        SetQuickBolusSettingsResponse parsedRes = (SetQuickBolusSettingsResponse) MessageTester.test(
                "004ed34e19002a6ff01fd8f817c3af77277060ce0451da1741ef3caa98d25b8a",
                78,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
    }
}