package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
}
