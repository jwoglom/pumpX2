package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.authentication.ThirdChallengeV2Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ThirdChallengeV2ResponseTest {
    // ./android-2024-02-29-6char2.csv
    @Test
    public void test_167cargo_response_split() throws DecoderException {
        //1805	103.080736	c2:48:14:3a:a4:c8 (tslim X2 ***941)	Google_aa:a4:cd (James' Pixel 7a)	ATT	141	00022502aa00000300174104634f4cfde021623a609c98311ae7508b41126542de029b76b1781de8f3db6e723950a8e64715c97cf44d80a6976b91ab846cdbf732bf160c1c0223f0a674a4734104732ff948fed2dbb06806502a7ae7ca0afdf1991eab79c866ff53e9d23bd4d1886f3cb55663b7979947f6a96d52511ac843b9d28b20805f263365bc50d1b93d7620bec12ade679a6b482ba5bfbe973e91dcdcd87bc3ab04090803db2945cca39e4c9be8	Rcvd Handle Value Notification, Handle: 0x0022 (Unknown)
        ThirdChallengeV2Response expected = new ThirdChallengeV2Response(
                0,
                new byte[]{3,0,23},
                new byte[]{
                        65,4,99,79,76,-3,-32,33,98,58,96,-100,-104,49,26,-25,80,-117,65,18,101,66,-34,2,-101,118,-79,120,29,-24,-13,-37,110,114,57,80,-88,-26,71,21,-55,124,-12,77,-128,-90,-105,107,-111,-85,-124,108,-37,-9,50,-65,22,12,28,2,35,-16,-90,116,-92,115,65,4,115,47,-7,72,-2,-46,-37,-80,104,6,80,42,122,-25,-54,10,-3,-15,-103,30,-85,121,-56,102,-1,83,-23,-46,59,-44,-47,-120,111,60,-75,86,99,-73,-105,-103,71,-10,-87,109,82,81,26,-56,67,-71,-46,-117,32,-128,95,38,51,101,-68,80,-47,-71,61,118,32,-66,-63,42,-34,103,-102,107,72,43,-91,-65,-66,-105,62,-111,-36,-36,-40,123,-61,-85,4,9,8,3,-37,41,69,-52,-93,-98,76
                }
        );

        ThirdChallengeV2Response parsedReq = (ThirdChallengeV2Response) MessageTester.test(
                "00022502aa00000300174104634f4cfde021623a609c98311ae7508b41126542de029b76b1781de8f3db6e723950a8e64715c97cf44d80a6976b91ab846cdbf732bf160c1c0223f0a674a4734104732ff948fed2dbb06806502a7ae7ca0afdf1991eab79c866ff53e9d23bd4d1886f3cb55663b7979947f6a96d52511ac843b9d28b20805f263365bc50d1b93d7620bec12ade679a6b482ba5bfbe973e91dcdcd87bc3ab04090803db2945cca39e4c9be8",
                2,
                10,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        MessageTester.assertHexEquals(expected.getCentralChallengeHash(), parsedReq.getCentralChallengeHash());
    }
}
