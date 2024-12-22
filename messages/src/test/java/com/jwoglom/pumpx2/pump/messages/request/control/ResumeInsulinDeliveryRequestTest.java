package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.ResumeInsulinDeliveryRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ResumeInsulinDeliveryRequestTest {
    @Test
    public void testResumeInsulinDeliveryRequest() throws DecoderException {
        initPumpState("authenticationKey", 0L);

        // empty cargo
        ResumeInsulinDeliveryRequest expected = new ResumeInsulinDeliveryRequest();

        ResumeInsulinDeliveryRequest parsedReq = (ResumeInsulinDeliveryRequest) MessageTester.test(
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