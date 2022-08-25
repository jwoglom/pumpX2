package com.jwoglom.pumpx2.shared;

import static org.apache.commons.codec.binary.Hex.encodeHex;
import static org.apache.commons.codec.binary.Hex.decodeHex;

import org.apache.commons.codec.DecoderException;

public class Hex {
    /**
     * AndroidAPS uses the org.apache.http.legacy library which has its own implementation of
     * org.apache.commons.codec.binary.Hex, which does not include encodeHexString, but does
     * include encodeHex. So this wrapper MUST be used in place of the actual Hex.encodeHexString.
     */
    public static String encodeHexString(final byte[] data) {
        return new String(encodeHex(data));
    }

    /**
     * wrap
     */
    public static byte[] decodeHex(String hex) throws DecoderException {
        return org.apache.commons.codec.binary.Hex.decodeHex(hex);
    }
}
