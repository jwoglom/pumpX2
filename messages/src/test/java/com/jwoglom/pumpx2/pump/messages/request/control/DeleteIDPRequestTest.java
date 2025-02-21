package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.DeleteIDPRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DeleteIDPRequestTest {
    @Test
    public void testDeleteIDPRequest_idpId2() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DeleteIDPRequest expected = new DeleteIDPRequest(2);

        DeleteIDPRequest parsedReq = (DeleteIDPRequest) MessageTester.test(
                "014dae4d1a0201c0493e208cb42b3ffcb6f3f0a9",
                77,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "004de51d3ea637ac1b02df4cce79c8"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
    @Test
    public void testDeleteIDPRequest_idpId1() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        DeleteIDPRequest expected = new DeleteIDPRequest(1);

        DeleteIDPRequest parsedReq = (DeleteIDPRequest) MessageTester.test(
                "0153ae531a0101cf493e200e7a1458b74d619de6",
                83,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0053faa1644c35799cdc81ac28666a"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}