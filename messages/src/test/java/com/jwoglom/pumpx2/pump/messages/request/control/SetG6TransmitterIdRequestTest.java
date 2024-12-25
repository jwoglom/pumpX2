package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetG6TransmitterIdRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetG6TransmitterIdRequestTest {
    @Test
    public void testSetG6TransmitterIdRequest_8L459R() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        // empty cargo
        SetG6TransmitterIdRequest expected = new SetG6TransmitterIdRequest(
                "8L459R"
        );

        SetG6TransmitterIdRequest parsedReq = (SetG6TransmitterIdRequest) MessageTester.test(
                "0221b02128384c34353952000000000000000000",
                33,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "012100da09f31f74a5fbd08dc7fc210ddd3cb101",
                "00218f1dc87134edb724ae"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals("8L459R", parsedReq.getTxId());
    }
}