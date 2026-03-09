package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class GetSavedG7PairingCodeResponseTest {
    @Test
    public void testSavedG7PairingCodeResponse_parseCargo() throws DecoderException {
        GetSavedG7PairingCodeResponse expected = new GetSavedG7PairingCodeResponse(
            7681
        );

        GetSavedG7PairingCodeResponse parsedRes = new GetSavedG7PairingCodeResponse();
        parsedRes.parse(Hex.decodeHex("011e"));

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(7681, parsedRes.getPairingCode());
    }
}
