package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class BolusPermissionResponseTest {
    @Test
    //@Ignore("TODO: remote bolus")
    public void testBolusPermissionResponse() throws DecoderException {
        // PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461457713
        // ./scripts/get-single-opcode.py '0034a3341e0100000000012f47811b5de43c94c520010c0203bff27993ffcf441f955c40'

        initPumpState("6VeDeRAL5DCigGw2", 461457713);
        // 461457713
        // 461457181
        
        BolusPermissionResponse expected = new BolusPermissionResponse(
            // int status, int bolusId, int nackReason
                0, 0, 0
        );

        BolusPermissionResponse parsedRes = (BolusPermissionResponse) MessageTester.test(
                // 0034a3341e0100000000012f47811b5de43c94c520010c0203bff27993ffcf441f955c40
                "0034a3341e0100000000012f47811b5de43c",
                52,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "94c520010c0203bff27993ffcf441f955c40"
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}