package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class EnterFillTubingModeResponseTest {
    @Test
    public void testEnterFillTubingModeResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        EnterFillTubingModeResponse expected = new EnterFillTubingModeResponse(
            0
        );

        EnterFillTubingModeResponse parsedRes = (EnterFillTubingModeResponse) MessageTester.test(
                "007a957a1900fea3ee1fe559e33c9fdc97594748e9c9631ee2db4458683f3412",
                122,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
    }
}