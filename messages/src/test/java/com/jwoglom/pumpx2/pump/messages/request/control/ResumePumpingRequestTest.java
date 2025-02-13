package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.ResumePumpingRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ResumePumpingRequestTest {
    @Test
    public void testResumePumpingRequest() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        // empty cargo
        ResumePumpingRequest expected = new ResumePumpingRequest();

        ResumePumpingRequest parsedReq = (ResumePumpingRequest) MessageTester.test(
                "013d9a3d1808eaee1ff9e90b186d391c9892ca8f",
                61,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "003d7bc20e2a68cc7bc5c6fcce"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}