package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ApiVersionRequestTest {
    @Test
    public void testApiVersion() throws DecoderException {
        // empty cargo
        ApiVersionRequest expected = new ApiVersionRequest();

        ApiVersionRequest parsedReq = (ApiVersionRequest) MessageTester.test(
                "0002200200382c",
                2,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

}
