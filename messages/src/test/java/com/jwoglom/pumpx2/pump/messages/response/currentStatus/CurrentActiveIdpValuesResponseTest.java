package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentActiveIdpValuesResponseTest {
    @Test
    public void testCurrentActiveIdpValuesResponse_parseCargo_CapturedPayload() throws DecoderException {
        // cargoHex: 7017000073002c012800
        byte[] rawCargo = Hex.decodeHex("7017000073002c012800");

        CurrentActiveIdpValuesResponse parsedRes = new CurrentActiveIdpValuesResponse();
        parsedRes.parse(rawCargo);

        assertHexEquals(rawCargo, parsedRes.getCargo());
        assertEquals(6000L, parsedRes.getCurrentCarbRatio());
        assertEquals(11264, parsedRes.getCurrentTargetBg());
        assertEquals(300, parsedRes.getCurrentInsulinDuration());
        assertEquals(40, parsedRes.getCurrentIsf());
    }
}
