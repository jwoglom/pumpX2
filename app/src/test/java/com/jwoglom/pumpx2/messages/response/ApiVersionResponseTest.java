package com.jwoglom.pumpx2.messages.response;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.ApiVersionResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ApiVersionResponseTest {
    @Test
    public void testApiVersionResponse() throws DecoderException {
        // Control-IQ pump v7.3.1
        ApiVersionResponse expected = new ApiVersionResponse(2, 0);

        ApiVersionResponse parsedRes = (ApiVersionResponse) MessageTester.test(
                "00022102040200000077c8",
                2,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertEquals(expected.getMajorVersion(), parsedRes.getMajorVersion());
        assertEquals(expected.getMinorVersion(), parsedRes.getMinorVersion());
    }
}
