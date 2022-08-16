package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.BolusPermissionRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

/**
 * BUG: tron.mergeIntoSinglePacket() does not match the packets expected
 */
public class BolusPermissionRequestTest {
    @Test
    public void testBolusPermissionRequest() throws DecoderException {
        // PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461457713
        // ./scripts/get-single-opcode.py '0134a234181d47811bc64bcd072fc978e4842046 003426b62ba72612b4f04978ac'

        initPumpState("6VeDeRAL5DCigGw2", 461457713);
        BolusPermissionRequest expected = new BolusPermissionRequest(new byte[]{
                29,71,-127,27,-58,75,-51,7,47,-55,120,-28,-124,32,70,38,-74,43,-89,38,18,-76,-16,73
        });


        String[] messages = new String[]{
                "0134a234181d47811bc64bcd072fc978e4842046",
                "003426b62ba72612b4f04978ac"
        };
        BolusPermissionRequest parsedReq = (BolusPermissionRequest) MessageTester.test(
                messages[0],
                52,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                messages[1]
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}