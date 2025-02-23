package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.ExitChangeCartridgeModeRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ExitChangeCartridgeModeRequestTest {
    @Test
    public void testExitChangeCartridgeModeRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        ExitChangeCartridgeModeRequest expected = new ExitChangeCartridgeModeRequest();

        ExitChangeCartridgeModeRequest parsedReq = (ExitChangeCartridgeModeRequest) MessageTester.test(
                "0189928918e40e4120bd5bce64b42a9df62d8cae",
                -119,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0089e8d1e8325cc6cacddc7790"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}