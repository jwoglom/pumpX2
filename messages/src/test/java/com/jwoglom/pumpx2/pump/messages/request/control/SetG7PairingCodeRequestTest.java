package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetG7PairingCodeRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetG7PairingCodeRequestTest {
    @Test
    public void testSetG7PairingCodeRequest_code3546() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);


        SetG7PairingCodeRequest expected = new SetG7PairingCodeRequest(3546);

        SetG7PairingCodeRequest parsedReq = (SetG7PairingCodeRequest) MessageTester.test(
                "02f9fcf920da0d000000000000e68cfd1fc0e13b",
                -7,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01f9ff4ae4968d2f256e803caa2487aeea5e8e20",
                "00f9f8"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(3546, parsedReq.getPairingCode());
    }
}