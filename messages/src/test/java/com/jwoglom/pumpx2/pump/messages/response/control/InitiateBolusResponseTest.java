package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class InitiateBolusResponseTest {
    /*
     * without signed request and response over current_status_characteristics:
     * ErrorResponse[errorCode=UNDEFINED_ERROR,errorCodeId=0,requestCodeId=0,cargo={0,0}]
     * with signed request and response over control:
     * 0000f919721b69bed65c347359b5b7697039a76055ee189a8e9d
     */
    @Test
    public void testInitiateBolusResponse() throws DecoderException { 
        initPumpState("authenticationKey", 0L);
        
        InitiateBolusResponse expected = new InitiateBolusResponse(
            // int status, int bolusId, int statusType
        );

        InitiateBolusResponse parsedRes = (InitiateBolusResponse) MessageTester.test(
                "xxxx",
                3,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}