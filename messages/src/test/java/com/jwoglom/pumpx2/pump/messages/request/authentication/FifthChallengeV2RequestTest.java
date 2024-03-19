package com.jwoglom.pumpx2.pump.messages.request.authentication;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class FifthChallengeV2RequestTest {
    // ./android-2024-02-29-6char2.csv
    @Test
    public void test_167cargo_fifthchallenge_split() throws DecoderException {
        FifthChallengeV2Request expected = new FifthChallengeV2Request(
                0,
                new byte[]{87,26,-48,52,116,28,119,125,0,0,0,0,0,0,0,0,47,-20,-32,-126,-118,-111,-9,55,42,71,-53,123,-11,-105,-94,-106,-106,31,102,-16,-12,92,75,123,118,-23,-82,-81,-113,23,111,109}
        );

        /*
    //1809	103.184105	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	03042804320000571ad034741c777d0000000000	Sent Write Command, Handle: 0x0022 (Unknown)
    //1810	103.187141	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	02040000002fece0828a91f7372a47cb7bf597a2	Sent Write Command, Handle: 0x0022 (Unknown)
    //1811	103.188685	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	010496961f66f0f45c4b7b76e9aeaf8f176f6d44	Sent Write Command, Handle: 0x0022 (Unknown)
    //1812	103.190121	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	15	00046e	Sent Write Command, Handle: 0x0022 (Unknown)
         */
        FifthChallengeV2Request parsedReq = (FifthChallengeV2Request) MessageTester.test(
                "03042804320000571ad034741c777d0000000000",
                4,
                4,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected,
                "02040000002fece0828a91f7372a47cb7bf597a2",
                "010496961f66f0f45c4b7b76e9aeaf8f176f6d44",
                "00046e"
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, parsedReq.getAppInstanceId());

    }
}