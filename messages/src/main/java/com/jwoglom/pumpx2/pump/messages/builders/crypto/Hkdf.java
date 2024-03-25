package com.jwoglom.pumpx2.pump.messages.builders.crypto;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import org.apache.commons.codec.DecoderException;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hkdf {
    public static byte[] build(byte[] nonce, byte[] keyMaterial) {
        return expandKey(updateMacWithKeyMaterial(nonce, keyMaterial), new byte[0], 32);
    }

    static byte[] expandKey(byte[] keyMaterial, byte[] nonce, int keyLength) {
        Mac mac = initHmacSha256(keyMaterial);
        if (mac == null) {
            return null;
        }

        byte[] previousBlock = new byte[0];
        byte[] resultKey = new byte[0];
        int numBlocks = (int) Math.ceil(keyLength / 32.0);
        for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
            previousBlock = updateMac(Bytes.combine(previousBlock, nonce, decodeHex(Integer.toHexString(blockIndex + 1))), mac);
            resultKey = Bytes.combine(resultKey, previousBlock);
        }
        return Arrays.copyOfRange(resultKey, 0, keyLength);
    }

    private static byte[] updateMacWithKeyMaterial(byte[] keyMaterial, byte[] data) {
        return updateMac(data, initHmacSha256(keyMaterial));
    }

    private static Mac initHmacSha256(byte[] keyMaterial) {
        Mac mac;
        try {
            mac = Mac.getInstance("hmacSHA256");
            mac.init(newSecretKeySpec(keyMaterial));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return null;
        }
        return mac;
    }

    private static Key newSecretKeySpec(byte[] keyMaterial) {
        if (keyMaterial.length == 0) {
            keyMaterial = new byte[32];
        }
        return new SecretKeySpec(keyMaterial, "HmacSHA256");
    }

    private static byte[] updateMac(byte[] bArr, Mac mac) {
        mac.update(bArr);
        byte[] doFinal = mac.doFinal();
        mac.reset();
        return doFinal;
    }

    private static byte[] decodeHex(String str) {
        if (str.length() % 2 == 1) {
            str = "0" + str;
        }

        int length = str.length() / 2;
        byte[] out = new byte[length];
        for (int i = 0; i < length; i++) {
            out[i] = (byte) Integer.parseInt(str.substring(i*2, (i*2) + 2), 16);
        }
        return out;
    }
}
