package com.jwoglom.pumpx2.pump.messages.request.authentication;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class FourthChallengeV2RequestTest {
    // ./android-2024-02-29-6char2.csv
    @Test
    public void test_167cargo_fourthchallenge_split() throws DecoderException {
        FourthChallengeV2Request expected = new FourthChallengeV2Request(
                new byte[]{0, 0}
        );

        FourthChallengeV2Request parsedReq = (FourthChallengeV2Request) MessageTester.test(
                "000326030200008121",
                3,
                1,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, parsedReq.getChallengeParam());

    }
}
