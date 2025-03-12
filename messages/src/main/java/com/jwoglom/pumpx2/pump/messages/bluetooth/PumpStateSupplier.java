package com.jwoglom.pumpx2.pump.messages.bluetooth;

import org.apache.commons.lang3.StringUtils;
import com.jwoglom.pumpx2.pump.messages.builders.crypto.Hkdf;
import com.jwoglom.pumpx2.pump.messages.models.ApiVersion;
import com.jwoglom.pumpx2.shared.Hex;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;

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

        // stored jpake raw derived secret is decoded from hex for use in hmac
        if (!StringUtils.isBlank(derivedSecret) && !StringUtils.isBlank(serverNonce)) {
            try {
                byte[] jpakeSecret = Hex.decodeHex(derivedSecret);
                byte[] jpakeNonce = Hex.decodeHex(serverNonce);

                // hkdf of nonce and secret bytes are passed to hmac
                byte[] authKey = Hkdf.build(jpakeNonce, jpakeSecret);
                L.d(TAG, "DETERMINE-PUMP-AUTH-KEY-LINE PUMP_AUTHENTICATION_KEY=" + Hex.encodeHexString(authKey) + " PUMP_JPAKE_DERIVED_SECRET=" + derivedSecret + " PUMP_JPAKE_SERVER_NONCE=" + serverNonce);

                return authKey;
            } catch (DecoderException e) {
                L.e(TAG, e);
            }
        }

        L.d(TAG, "DETERMINE-PUMP-AUTH-KEY-LINE PUMP_AUTHENTICATION_KEY=" + code + " PUMP_JPAKE=NULL");


        if (code == null) return new byte[0];

        // 16 digit pairing code is passed as raw ascii to hmac
        return code.getBytes(StandardCharsets.UTF_8);
    }
}
