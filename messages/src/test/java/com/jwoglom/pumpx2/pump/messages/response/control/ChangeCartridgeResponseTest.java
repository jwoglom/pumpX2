package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class ChangeCartridgeResponseTest {

    // these were bt log parsing errors
//    /**
//     * with no message signing on request or response, returns cargo of 100 100 followed by 0s TO CURRENT_STATUS
//     *
//     * with message signing on request and response, returns:
//     * ErrorResponse[errorCode=UNDEFINED_ERROR,errorCodeId=0,requestCodeId=0,cargo={0,0}]
//     * (invalid message signing?)
//     */
//    @Test
//    public void testChangeCartridgeResponseNoSignature() throws DecoderException {
//        initPumpState("authenticationKey", 0L);
//
//        ChangeCartridgeResponse expected = new ChangeCartridgeResponse(
//            // int status, int unknown1
//            100, 100
//        );
//
//        ChangeCartridgeResponse parsedRes = (ChangeCartridgeResponse) MessageTester.test(
//                "000491040b6464000000000000000000d201",
//                4,
//                1,
//                CharacteristicUUID.CONTROL_CHARACTERISTICS,
//                expected
//        );
//
//        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
//    }
//
//    @Test
//    public void testChangeCartridgeResponse_2() throws DecoderException {
//        initPumpState("authenticationKey", 0L);
//
//        ChangeCartridgeResponse expected = new ChangeCartridgeResponse(
//                new byte[]{87,90,0,-103,0,-123,0,113,15,-100,-1}
//        );
//
//        ChangeCartridgeResponse parsedRes = (ChangeCartridgeResponse) MessageTester.test(
//                "007691760b575a0099008500710f9cff51ac",
//                118,
//                1,
//                CharacteristicUUID.CONTROL_CHARACTERISTICS,
//                expected
//        );
//
//        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
//    }


    @Test
    public void testChangeCartridgeResponse_2() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        ChangeCartridgeResponse expected = new ChangeCartridgeResponse(
                0
        );

        ChangeCartridgeResponse parsedRes = (ChangeCartridgeResponse) MessageTester.test(
                "003691361900e2cd272051bfc76d9666fb6bbe8b89627199c182617b1f6fe440",
                54,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}