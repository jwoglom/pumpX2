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
    public void testBolusPermissionResponse() throws DecoderException {
        // PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461510588
        // ./scripts/get-single-opcode.py '00efa3ef1e009a29000000c317821b8a7f9a9bd41e220a20856446e2ff9c69df60ab62987c'

        initPumpState("6VeDeRAL5DCigGw2", 461510588);

        // BolusPermissionResponse[bolusId=10650,nackReason=0,status=0,cargo={0,-102,41,0,0,0,-61,23,-126,27,-118,127,-102,-101,-44,30,34,10,32,-123,100,70,-30,-1,-100,105,-33,96,-85,98}]
        BolusPermissionResponse expected = new BolusPermissionResponse(
                // int status, int bolusId, int nackReason
                0, 10650, 0
        );

        BolusPermissionResponse parsedRes = (BolusPermissionResponse) MessageTester.test(
                // 0034a3341e0100000000012f47811b5de43c94c520010c0203bff27993ffcf441f955c40
                "00efa3ef1e009a29000000c317821b8a7f9a9bd41e220a20856446e2ff9c69df60ab62987c",
                -17, // TODO: this is likely a wraparound (https://github.com/jwoglom/pumpX2/issues/5)
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    @Ignore("skip")
    public void testBolusPermissionResponse_Skip() throws DecoderException {
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