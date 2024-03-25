package com.jwoglom.pumpx2.pump.messages.builders;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.builders.crypto.Hkdf;
import com.jwoglom.pumpx2.pump.messages.builders.crypto.HmacSha256;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.ComparisonFailure;
import org.junit.Test;

public class TestCrypto {
    @Test(expected=ComparisonFailure.class)
    public void testCryptoHmacAndHkdfNotIdentical() throws DecoderException {
        byte[] nonce = Hex.decodeHex("e734344901549417");
        byte[] key = Hex.decodeHex("e734344901549417f6243f8e4a712f87ae9409476f8d022c347ff690249683aa");
        byte[] hkdf = Hkdf.build(nonce, key);
        byte[] hmac = HmacSha256.hmacSha256(key, nonce);
        assertHexEquals(hkdf, hmac);
    }

    @Test(expected=ComparisonFailure.class)
    public void testCryptoHmacAndHkdfNotIdentical_reversed() throws DecoderException {
        byte[] nonce = Hex.decodeHex("e734344901549417");
        byte[] key = Hex.decodeHex("e734344901549417f6243f8e4a712f87ae9409476f8d022c347ff690249683aa");
        byte[] hkdf = Hkdf.build(nonce, key);
        byte[] hmac = HmacSha256.hmacSha256(nonce, key);
        assertHexEquals(hkdf, hmac);
    }
}
