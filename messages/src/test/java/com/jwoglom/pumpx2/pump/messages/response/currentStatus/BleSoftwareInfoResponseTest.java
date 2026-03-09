package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BleSoftwareInfoResponseTest {
    @Test
    public void testBleSoftwareInfoResponse_parseCargo() throws DecoderException {
        BleSoftwareInfoResponse expected = new BleSoftwareInfoResponse(
                132, 7, 2, 0, 10, 257
        );

        BleSoftwareInfoResponse parsedRes = new BleSoftwareInfoResponse(
                Hex.decodeHex("84000700020000000a0000000101")
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(132, parsedRes.getSoftDeviceId());
        assertEquals(7, parsedRes.getSoftDeviceMajorVersion());
        assertEquals(2, parsedRes.getSoftDeviceMinorVersion());
        assertEquals(0, parsedRes.getSoftDeviceBugfixVersion());
        assertEquals(10, parsedRes.getSoftDeviceVersion());
        assertEquals(257, parsedRes.getSoftDeviceSubVersion());
    }
}
