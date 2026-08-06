package com.jwoglom.pumpx2.pump.messages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class PacketArrayListTest {
    private static boolean shouldIgnoreInvalidHmac(PacketArrayList accumulator, byte[] authKey) throws Exception {
        Method method = PacketArrayList.class.getDeclaredMethod("shouldIgnoreInvalidHmac", byte[].class);
        method.setAccessible(true);
        return (Boolean) method.invoke(accumulator, (Object) authKey);
    }

    private static PacketArrayList newAccumulator() {
        return new PacketArrayList((byte) 1, (byte) 0, (byte) 1, false);
    }

    @Test
    public void testShouldIgnoreInvalidHmacWithShortAuthKeyDoesNotThrow() throws Exception {
        // A legacy 16-character pairing code is shorter than the 27-byte ignore prefix.
        byte[] legacyPairingCode = "1234567890ABCDEF".getBytes(StandardCharsets.US_ASCII);
        assertFalse(shouldIgnoreInvalidHmac(newAccumulator(), legacyPairingCode));
    }

    @Test
    public void testShouldIgnoreInvalidHmacWithNullAuthKeyDoesNotThrow() throws Exception {
        assertFalse(shouldIgnoreInvalidHmac(newAccumulator(), null));
    }

    @Test
    public void testShouldIgnoreInvalidHmacRecognizesFullPrefix() throws Exception {
        byte[] key = (PacketArrayList.IGNORE_INVALID_HMAC + "-suffix").getBytes(StandardCharsets.US_ASCII);
        assertTrue(shouldIgnoreInvalidHmac(newAccumulator(), key));
    }
}
