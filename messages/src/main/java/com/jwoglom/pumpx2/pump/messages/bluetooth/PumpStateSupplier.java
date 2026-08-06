package com.jwoglom.pumpx2.pump.messages.bluetooth;

import org.apache.commons.lang3.StringUtils;

import com.jwoglom.pumpx2.pump.messages.builders.crypto.Hkdf;
import com.jwoglom.pumpx2.pump.messages.models.ApiVersion;
import com.jwoglom.pumpx2.shared.Hex;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.DecoderException;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class PumpStateSupplier {
    private static final String TAG = "PumpStateSupplier";

    public static Supplier<byte[]> authenticationKey = PumpStateSupplier::determinePumpAuthKey;
    public static Supplier<String> pumpPairingCode = null;
    public static Supplier<String> jpakeDerivedSecretHex = null;
    public static Supplier<String> jpakeServerNonceHex = null;
    public static Supplier<Long> pumpTimeSinceReset = null;
    public static Supplier<ApiVersion> pumpApiVersion = null;
    public static Supplier<Boolean> controlIQSupported = () -> false;
    public static Supplier<Boolean> actionsAffectingInsulinDeliveryEnabled = () -> false;
    public static Supplier<Integer> inProgressBolusId = () -> null;


    /**
     * @return the byte array consisting of the key used for signing messages
     */
    private static byte[] determinePumpAuthKey() {
        String derivedSecret = jpakeDerivedSecretHex == null ? null : jpakeDerivedSecretHex.get();
        String serverNonce = jpakeServerNonceHex == null ? null : jpakeServerNonceHex.get();
        String code = pumpPairingCode == null ? null : pumpPairingCode.get();

        if (StringUtils.isBlank(derivedSecret) && StringUtils.isBlank(code)) {
            throw new IllegalStateException("no pump authenticationKey");
        }

        // The JPAKE auth key is derived from BOTH halves, so it can only be built once both are
        // present. Having exactly one is a normal, transient state rather than an error: a
        // SHORT_6CHAR re-pair (TandemPump.pair(), "CONFIRM" path) deliberately keeps the stored
        // derived secret while blanking the server nonce, and the nonce is only repopulated when
        // the handshake completes. Treat that window as "no JPAKE key yet" and fall back to the
        // pairing code, which is what the handshake itself authenticates with.
        boolean hasDerivedSecret = !StringUtils.isBlank(derivedSecret);
        boolean hasServerNonce = !StringUtils.isBlank(serverNonce);

        if (hasDerivedSecret && hasServerNonce) {
            // stored jpake raw derived secret is decoded from hex for use in hmac
            try {
                byte[] jpakeSecret = Hex.decodeHex(derivedSecret);
                byte[] jpakeNonce = Hex.decodeHex(serverNonce);

                // hkdf of nonce and secret bytes are passed to hmac
                return Hkdf.build(jpakeNonce, jpakeSecret);
            } catch (DecoderException e) {
                // Both values are present but unparseable, which means the persisted state is
                // corrupt. Falling back would silently sign with the wrong key, so fail loudly.
                throw new IllegalStateException("invalid JPAKE derived secret/server nonce hex", e);
            }
        }

        if (hasDerivedSecret != hasServerNonce) {
            L.d(TAG, "JPAKE state is incomplete (derivedSecret="
                    + (hasDerivedSecret ? "set" : "unset") + ", serverNonce="
                    + (hasServerNonce ? "set" : "unset")
                    + "); falling back to the pairing code");
        }

        if (code == null) return new byte[0];

        // 16 digit pairing code is passed as raw ascii to hmac
        return code.getBytes(StandardCharsets.UTF_8);
    }
}
