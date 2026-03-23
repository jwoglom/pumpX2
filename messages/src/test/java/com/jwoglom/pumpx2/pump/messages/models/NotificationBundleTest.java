package com.jwoglom.pumpx2.pump.messages.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ActiveAamBitsRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ActiveAamBitsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HighestAamResponse;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Test
    public void testApiVersionConstructorAndSetter() {
        NotificationBundle bundle = new NotificationBundle(KnownApiVersion.API_V2_5.get());
        assertEquals(KnownApiVersion.API_V2_5.get().serialize(), bundle.getApiVersion().serialize());

        bundle.setApiVersion(KnownApiVersion.MOBI_API_V3_5.get());
        assertEquals(KnownApiVersion.MOBI_API_V3_5.get().serialize(), bundle.getApiVersion().serialize());

        NotificationBundle copied = new NotificationBundle(bundle);
        assertEquals(KnownApiVersion.MOBI_API_V3_5.get().serialize(), copied.getApiVersion().serialize());

        bundle.setApiVersion(null);
        assertNull(bundle.getApiVersion());
    }

    @Test
    public void testAllRequestsFiltersUnsupportedApiDependentRequests() {
        Set<Class<? extends Message>> oldApiClasses = NotificationBundle.allRequests(KnownApiVersion.API_V2_1.get())
                .stream().map(Message::getClass).collect(Collectors.toSet());
        assertFalse(oldApiClasses.contains(ActiveAamBitsRequest.class));

        Set<Class<? extends Message>> mobiApiClasses = NotificationBundle.allRequests(KnownApiVersion.MOBI_API_V3_5.get())
                .stream().map(Message::getClass).collect(Collectors.toSet());
        assertTrue(mobiApiClasses.contains(ActiveAamBitsRequest.class));
    }

    @Test
    public void testAllRequestsForPumpUsesBundleApiVersion() {
        NotificationBundle bundle = new NotificationBundle(KnownApiVersion.API_V2_1.get());
        Set<Class<? extends Message>> requestClasses = bundle.allRequestsForPump()
                .stream().map(Message::getClass).collect(Collectors.toSet());
        assertFalse(requestClasses.contains(ActiveAamBitsRequest.class));

        bundle.setApiVersion(KnownApiVersion.MOBI_API_V3_5.get());
        Set<Class<? extends Message>> mobiRequestClasses = bundle.allRequestsForPump()
                .stream().map(Message::getClass).collect(Collectors.toSet());
        assertTrue(mobiRequestClasses.contains(ActiveAamBitsRequest.class));
    }
}
