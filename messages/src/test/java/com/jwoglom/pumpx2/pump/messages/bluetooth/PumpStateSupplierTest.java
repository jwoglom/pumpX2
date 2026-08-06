package com.jwoglom.pumpx2.pump.messages.bluetooth;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

import java.nio.charset.StandardCharsets;

import com.jwoglom.pumpx2.pump.messages.builders.crypto.Hkdf;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.After;
import org.junit.Test;

public class PumpStateSupplierTest {
    @After
    public void tearDown() {
        PumpStateSupplier.pumpPairingCode = null;
        PumpStateSupplier.jpakeDerivedSecretHex = null;
        PumpStateSupplier.jpakeServerNonceHex = null;
    }

    @Test
    public void testInvalidJpakeHexFailsFastInsteadOfFallingBackToPairingCode() {
        PumpStateSupplier.pumpPairingCode = () -> "1234567890ABCDEF";
        PumpStateSupplier.jpakeDerivedSecretHex = () -> "nothex";
        PumpStateSupplier.jpakeServerNonceHex = () -> "001122";

        assertThrows(IllegalStateException.class, PumpStateSupplier.authenticationKey::get);
    }

    /**
     * TandemPump.pair() on the SHORT_6CHAR "CONFIRM" path deliberately keeps the stored JPAKE
     * derived secret while blanking the server nonce, and the nonce is only repopulated once the
     * handshake completes. Throwing during that window breaks re-pairing: BTResponseParser calls
     * authenticationKey.get() unguarded for signed messages, so the exception surfaces as an
     * UNPROCESSABLE_MESSAGE critical error instead of the message being validated.
     */
    @Test
    public void testMidHandshakeJpakeStateFallsBackToPairingCode() {
        PumpStateSupplier.pumpPairingCode = () -> "1234567890ABCDEF";
        PumpStateSupplier.jpakeDerivedSecretHex = () -> "001122";
        PumpStateSupplier.jpakeServerNonceHex = () -> "";

        assertArrayEquals(
                "1234567890ABCDEF".getBytes(StandardCharsets.UTF_8),
                PumpStateSupplier.authenticationKey.get());
    }

    @Test
    public void testServerNonceWithoutDerivedSecretFallsBackToPairingCode() {
        PumpStateSupplier.pumpPairingCode = () -> "1234567890ABCDEF";
        PumpStateSupplier.jpakeServerNonceHex = () -> "001122";

        assertArrayEquals(
                "1234567890ABCDEF".getBytes(StandardCharsets.UTF_8),
                PumpStateSupplier.authenticationKey.get());
    }

    @Test
    public void testCompleteJpakeStateDerivesHkdfKey() throws DecoderException {
        PumpStateSupplier.pumpPairingCode = () -> "1234567890ABCDEF";
        PumpStateSupplier.jpakeDerivedSecretHex = () -> "00112233";
        PumpStateSupplier.jpakeServerNonceHex = () -> "44556677";

        assertArrayEquals(
                Hkdf.build(Hex.decodeHex("44556677"), Hex.decodeHex("00112233")),
                PumpStateSupplier.authenticationKey.get());
    }

    @Test
    public void testNoPairingCodeAndNoJpakeStateFailsFast() {
        assertThrows(IllegalStateException.class, PumpStateSupplier.authenticationKey::get);
    }

    @Test
    public void testLegacyPairingCodeIsUsedAsAsciiKey() {
        PumpStateSupplier.pumpPairingCode = () -> "1234567890ABCDEF";

        assertArrayEquals(
                "1234567890ABCDEF".getBytes(StandardCharsets.UTF_8),
                PumpStateSupplier.authenticationKey.get());
    }
}
