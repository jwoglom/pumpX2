package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class UserInteractionResponseTest {
    @Test
    public void testUserInteractionResponse_parseCargo_CapturedPayload() throws DecoderException {
        // cargoHex: 00
        byte[] rawCargo = Hex.decodeHex("00");

        UserInteractionResponse parsedRes = new UserInteractionResponse();
        parsedRes.parse(rawCargo);

        assertHexEquals(rawCargo, parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
    }
}
