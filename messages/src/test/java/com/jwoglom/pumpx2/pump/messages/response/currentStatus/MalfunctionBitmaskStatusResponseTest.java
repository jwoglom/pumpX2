package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.math.BigInteger;

public class MalfunctionBitmaskStatusResponseTest {
    @Test
    public void testMalfunctionBitmaskStatusResponse_parseBitmask() {
        BigInteger bitmask = BigInteger.ZERO.setBit(0).setBit(13).setBit(25);
        MalfunctionBitmaskStatusResponse response = new MalfunctionBitmaskStatusResponse(bitmask);

        assertEquals(bitmask, response.getBitmask());
        assertTrue(response.hasMalfunctionBit(0));
        assertTrue(response.hasMalfunctionBit(13));
        assertTrue(response.hasMalfunctionBit(25));
        assertTrue(response.getMalfunctions().contains(MalfunctionBitmaskStatusResponse.MalfunctionType.SOFTWARE));
        assertTrue(response.getMalfunctions().contains(MalfunctionBitmaskStatusResponse.MalfunctionType.PERIPH_POWER));
        assertTrue(response.getMalfunctions().contains(MalfunctionBitmaskStatusResponse.MalfunctionType.PUSHOFF_STALL));
        assertEquals(3, response.size());
    }

    @Test
    public void testMalfunctionBitmaskStatusResponse_zeroBitmaskFixture() throws DecoderException {
        MalfunctionBitmaskStatusResponse expected = new MalfunctionBitmaskStatusResponse(BigInteger.ZERO);

        MalfunctionBitmaskStatusResponse parsedRes = new MalfunctionBitmaskStatusResponse();
        parsedRes.parse(Hex.decodeHex("0000000000000000"));

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(BigInteger.ZERO, parsedRes.getBitmask());
        assertTrue(parsedRes.getMalfunctions().isEmpty());
        assertEquals(0, parsedRes.size());
    }
}
