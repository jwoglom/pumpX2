package com.jwoglom.pumpx2.pump.messages.models;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ActiveAamBitsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HighestAamResponse;

import org.junit.Test;

import java.math.BigInteger;

public class NotificationBundleTest {
    @Test
    public void testHighestAamSuppressedWhenNoMatchingActiveMalfunction() {
        HighestAamResponse highest = new HighestAamResponse(12, 0x2071, new byte[]{4, -1, -1});
        ActiveAamBitsResponse activeMismatch = new ActiveAamBitsResponse(
            BigInteger.ZERO,
            BigInteger.ZERO.setBit(11),
            ActiveAamBitsResponse.AamType.MALFUNCTION
        );

        NotificationBundle bundle = new NotificationBundle();
        bundle.add(highest);
        bundle.add(activeMismatch);

        assertEquals(0, bundle.getNotificationCount());
    }

    @Test
    public void testHighestAamIncludedWhenMatchingActiveMalfunction() {
        HighestAamResponse highest = new HighestAamResponse(12, 0x2071, new byte[]{4, -1, -1});
        ActiveAamBitsResponse activeMatch = new ActiveAamBitsResponse(
            BigInteger.ZERO,
            BigInteger.ZERO.setBit(12),
            ActiveAamBitsResponse.AamType.MALFUNCTION
        );

        NotificationBundle bundle = new NotificationBundle();
        bundle.add(highest);
        bundle.add(activeMatch);

        assertEquals(1, bundle.getNotificationCount());
    }
}
