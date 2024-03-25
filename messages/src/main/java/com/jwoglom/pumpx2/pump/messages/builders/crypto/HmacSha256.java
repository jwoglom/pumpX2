package com.jwoglom.pumpx2.pump.messages.builders.crypto;

import com.jwoglom.pumpx2.shared.L;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacSha256 {
    private static final String TAG = "HmacSha256";


    private static byte mod255(int i) {
        if (i < 0) {
            return (byte) ((i + 255 + 1) & 255);
        }
        return (byte) i;
    }

    private static byte[] mod255(byte[] data) {
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            if (b < 0) {
                data[i] = mod255(b);
            }
        }
        return data;
    }

    public static byte[] hmacSha256(byte[] data, byte[] key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(mod255(key), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            return mac.doFinal(mod255(data));
        } catch (Exception e) {
            L.e(TAG, e);
            return new byte[]{};
        }
    }
}
