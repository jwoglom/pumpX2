package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.EnterFillTubingModeRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class EnterFillTubingModeRequestTest {
    @Test
    public void testEnterFillTubingModeRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        EnterFillTubingModeRequest expected = new EnterFillTubingModeRequest();

        EnterFillTubingModeRequest parsedReq = (EnterFillTubingModeRequest) MessageTester.test(
                "017a947a1851eaee1f5595738c382cc5dc5552a2",
                122,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "007a4aa8482c322b5fd69805e8"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}