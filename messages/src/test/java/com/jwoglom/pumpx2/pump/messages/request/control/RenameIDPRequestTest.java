package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.RenameIDPRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class RenameIDPRequestTest {
    @Test
    public void testRenameIDPRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        RenameIDPRequest expected = new RenameIDPRequest(
                1,
                "testprofil2"
        );

        RenameIDPRequest parsedReq = (RenameIDPRequest) MessageTester.test(
                "02d6a8d62b01017465737470726f66696c320000",
                -42,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01d600000000e4483e2065ee618e7bd6a980f0a6",
                "00d60656ac383c1140bccf5a20be"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}