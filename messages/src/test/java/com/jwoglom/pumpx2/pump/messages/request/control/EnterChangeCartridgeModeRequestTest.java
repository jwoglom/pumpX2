package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class EnterChangeCartridgeModeRequestTest {
    // invalid
//    @Test
//    public void testChangeCartridgeRequest() throws DecoderException {
//        initPumpState("authenticationKey", 0L);
//
//        // empty cargo
//        ChangeCartridgeRequest expected = new ChangeCartridgeRequest();
//
//        ChangeCartridgeRequest parsedReq = (ChangeCartridgeRequest) MessageTester.test(
//                "00769076005a16",
//                118,
//                1,
//                CharacteristicUUID.CONTROL_CHARACTERISTICS,
//                expected
//        );
//
//        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
//    }

    @Test
    public void testChangeCartridgeRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        // empty cargo
        EnterChangeCartridgeModeRequest expected = new EnterChangeCartridgeModeRequest();

        EnterChangeCartridgeModeRequest parsedReq = (EnterChangeCartridgeModeRequest) MessageTester.test(
                "01369036181d142820db51e5fa626a7df87fc2cf",
                54,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "003621097bdd2395333e5d7d63"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}