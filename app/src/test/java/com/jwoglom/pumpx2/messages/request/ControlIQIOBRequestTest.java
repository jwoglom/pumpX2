package com.jwoglom.pumpx2.messages.request;

import static com.jwoglom.pumpx2.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.request.ControlIQIOBRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQIOBRequestTest {
    @Test
    public void testControlIQIOBRequest() throws DecoderException {
        // empty cargo
        ControlIQIOBRequest expected = new ControlIQIOBRequest();

        ControlIQIOBRequest parsedReq = (ControlIQIOBRequest) MessageTester.test(
                "00046c040052ee",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
