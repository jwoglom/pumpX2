package com.jwoglom.pumpx2.pump.messages.response.authentication;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class Jpake3SessionKeyResponseTest {
    // ./android-2024-02-29-6char2.csv
    @Test
    public void test_167cargo_4response_split() throws DecoderException {
        // 1808	103.174065	c2:48:14:3a:a4:c8 (tslim X2 ***941)	Google_aa:a4:cd (James' Pixel 7a)	ATT	37	0003270312000002e91fff505adb4f0000000000000000ba54	Rcvd Handle Value Notification, Handle: 0x0022 (Unknown)
        Jpake3SessionKeyResponse expected = new Jpake3SessionKeyResponse(
                new byte[]{0,0,2,-23,31,-1,80,90,-37,79,0,0,0,0,0,0,0,0}
        );

        Jpake3SessionKeyResponse parsedReq = (Jpake3SessionKeyResponse) MessageTester.test(
                "0003270312000002e91fff505adb4f0000000000000000ba54",
                3,
                2,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertHexEquals(expected.getUnknownField1(), parsedReq.getUnknownField1());
    }
}
