package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class Jpake1bResponseTest {
    // ./android-2024-02-29-6char2.csv
    @Test
    public void test_167cargo_response_split() throws DecoderException {
        //1787	102.842949	c2:48:14:3a:a4:c8 (tslim X2 ***941)	Google_aa:a4:cd (James' Pixel 7a)	ATT	186	00012301a700004104dc82c0b7f60e601ebed41ebafac79dac6b23055d6c2949e3bd7643acd951c400ca60513dbff125da5238e0a7eee27ff4533afded0725ad2804987c90646ade0f41048177dda93af133fcfcc3a78408af82370d76af3ecfe78bc16c732310ed00b188ae0b4c2769876ac29d6c65a205c96dd518e3166aa57d61bca1a6756aabbe4f6920c472ac523abdd69e678149c128daa073861c6c9371f04254d158b6481f2226ba3927	Rcvd Handle Value Notification, Handle: 0x0022 (Unknown)
        Jpake1bResponse expected = new Jpake1bResponse(
                0,
                new byte[]{
                        65, 4, -36, -126, -64, -73, -10, 14, 96, 30, -66, -44, 30, -70, -6, -57, -99, -84, 107, 35, 5, 93, 108, 41, 73, -29, -67, 118, 67, -84, -39, 81, -60, 0, -54, 96, 81, 61, -65, -15, 37, -38, 82, 56, -32, -89, -18, -30, 127, -12, 83, 58, -3, -19, 7, 37, -83, 40, 4, -104, 124, -112, 100, 106, -34, 15, 65, 4, -127, 119, -35, -87, 58, -15, 51, -4, -4, -61, -89, -124, 8, -81, -126, 55, 13, 118, -81, 62, -49, -25, -117, -63, 108, 115, 35, 16, -19, 0, -79, -120, -82, 11, 76, 39, 105, -121, 106, -62, -99, 108, 101, -94, 5, -55, 109, -43, 24, -29, 22, 106, -91, 125, 97, -68, -95, -90, 117, 106, -85, -66, 79, 105, 32, -60, 114, -84, 82, 58, -67, -42, -98, 103, -127, 73, -63, 40, -38, -96, 115, -122, 28, 108, -109, 113, -16, 66, 84, -47, 88, -74, 72, 31, 34, 38, -70
                }
        );

        Jpake1bResponse parsedReq = (Jpake1bResponse) MessageTester.test(
                "00012301a700004104dc82c0b7f60e601ebed41ebafac79dac6b23055d6c2949e3bd7643acd951c400ca60513dbff125da5238e0a7eee27ff4533afded0725ad2804987c90646ade0f41048177dda93af133fcfcc3a78408af82370d76af3ecfe78bc16c732310ed00b188ae0b4c2769876ac29d6c65a205c96dd518e3166aa57d61bca1a6756aabbe4f6920c472ac523abdd69e678149c128daa073861c6c9371f04254d158b6481f2226ba3927",
                1,
                10,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        MessageTester.assertHexEquals(expected.getCentralChallengeHash(), parsedReq.getCentralChallengeHash());
        MessageTester.assertHexEquals(
                Hex.decodeHex("4104dc82c0b7f60e601ebed41ebafac79dac6b23055d6c2949e3bd7643acd951c400ca60513dbff125da5238e0a7eee27ff4533afded0725ad2804987c90646ade0f41048177dda93af133fcfcc3a78408af82370d76af3ecfe78bc16c732310ed00b188ae0b4c2769876ac29d6c65a205c96dd518e3166aa57d61bca1a6756aabbe4f6920c472ac523abdd69e678149c128daa073861c6c9371f04254d158b6481f2226ba"),
                parsedReq.getCentralChallengeHash());
    }
}
