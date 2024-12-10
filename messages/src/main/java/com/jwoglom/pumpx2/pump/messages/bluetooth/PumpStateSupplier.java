package com.jwoglom.pumpx2.pump.messages.bluetooth;

import com.google.common.base.Strings;
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


    private static byte[] determinePumpAuthKey() {
        String derivedSecret = jpakeDerivedSecretHex == null ? null : jpakeDerivedSecretHex.get();
        String serverNonce = jpakeServerNonceHex == null ? null : jpakeServerNonceHex.get();
        String code = pumpPairingCode == null ? null : pumpPairingCode.get();

        if (derivedSecret == null && code == null) {
            throw new IllegalStateException("no pump authenticationKey");
        }

        // stored jpake raw derived secret is decoded from hex for use in hmac
        if (!Strings.isNullOrEmpty(derivedSecret) && !Strings.isNullOrEmpty(serverNonce)) {
            try {
                L.i(TAG, "PUMP_JPAKE_DERIVED_SECRET=" + derivedSecret + " PUMP_JPAKE_SERVER_NONCE=" + serverNonce);
                byte[] jpakeSecret = Hex.decodeHex(derivedSecret);
                byte[] jpakeNonce = Hex.decodeHex(serverNonce);
                byte[] authKey = Hkdf.build(jpakeNonce, jpakeSecret);
                L.i(TAG, "PUMP_AUTHENTICATION_KEY=" + Hex.encodeHexString(authKey) + " PUMP_JPAKE_DERIVED_SECRET=" + derivedSecret + " PUMP_JPAKE_SERVER_NONCE=" + serverNonce);

                return authKey;
            } catch (DecoderException e) {
                L.e(TAG, e);
            }
        }

        L.i(TAG, "PUMP_AUTHENTICATION_KEY=" + code);

        // pairing code is passed as raw ascii to hmac
        return code.getBytes(StandardCharsets.UTF_8);
    }
}
