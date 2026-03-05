package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.math.BigInteger;

public class ActiveAamBitsResponseTest {
    @Test
    public void testActiveAamBitsResponse_parseMalfunctionBitmasks() {
        BigInteger unacknowledged = BigInteger.ZERO.setBit(9);
        BigInteger active = BigInteger.ZERO.setBit(12).setBit(13);

        ActiveAamBitsResponse response = new ActiveAamBitsResponse(
            ActiveAamBitsResponse.buildCargo(
                unacknowledged,
                active,
                ActiveAamBitsResponse.AamType.MALFUNCTION
            )
        );

        assertEquals(ActiveAamBitsResponse.AamType.MALFUNCTION, response.getAamType());
        assertTrue(response.hasUnacknowledgedBit(9));
        assertTrue(response.hasActiveBit(12));
        assertTrue(response.hasActiveBit(13));
        assertTrue(response.getActiveMalfunctions().contains(MalfunctionBitmaskStatusResponse.MalfunctionType.VIBE));
        assertTrue(response.getActiveMalfunctions().contains(MalfunctionBitmaskStatusResponse.MalfunctionType.PERIPH_POWER));
        assertEquals(2, response.size());
    }
}
