package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ExtendedBolusStatusV2ResponseTest {
    @Test
    public void testExtendedBolusStatusV2Response_parseCargo() throws DecoderException {
        byte[] raw = Hex.decodeHex("00000022e700000000000000000000000000ffffffff");

        ExtendedBolusStatusV2Response parsedRes = new ExtendedBolusStatusV2Response();
        parsedRes.parse(raw);

        assertHexEquals(raw, parsedRes.getCargo());
        assertEquals(0, parsedRes.getBolusId());
        assertEquals(0, parsedRes.getBolusSource());
        assertEquals(0, parsedRes.getBolusStatus());
        assertEquals(0, parsedRes.getDuration());
        assertEquals(0, parsedRes.getRequestedVolume());
        assertEquals(0, parsedRes.getTimestamp());
        assertEquals(4294967295L, parsedRes.getSecondsSincePumpReset());
    }
}
