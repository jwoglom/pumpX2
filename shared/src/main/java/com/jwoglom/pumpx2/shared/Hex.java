package com.jwoglom.pumpx2.shared;

import org.apache.commons.codec.DecoderException;

public class Hex {
    /**
     * AndroidAPS uses the org.apache.http.legacy library which has its own implementation of
     * org.apache.commons.codec.binary.Hex which is missing several methods.
     */

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    public static String encodeHexString(final byte[] data) {
        // reimplementation of org.apache.commons.codec.binary.Hex.encodeHexString(data);
        if (data == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            // Unsigned right shift by 4 bits for high nibble
            sb.append(HEX_CHARS[(b >> 4) & 0x0F]);
            // Mask low nibble
            sb.append(HEX_CHARS[b & 0x0F]);
        }
        return sb.toString();
    }

    public static byte[] decodeHex(String hex) throws DecoderException {
        // reimplementation of org.apache.commons.codec.binary.Hex.decodeHex(hex);
        if (hex == null) {
            throw new DecoderException("Input hex string is null");
        }
        if (hex.length() % 2 != 0) {
            throw new DecoderException("Odd length for hex string");
        }
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            int high = toDigit(hex.charAt(i), i) << 4;
            int low = toDigit(hex.charAt(i + 1), i + 1);
            result[i / 2] = (byte) (high + low);
        }
        return result;
    }

    private static int toDigit(char ch, int index) throws DecoderException {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new DecoderException("Invalid hex character: " + ch + " at index " + index);
        }
        return digit;
    }
}
