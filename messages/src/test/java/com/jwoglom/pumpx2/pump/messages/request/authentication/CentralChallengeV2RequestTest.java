package com.jwoglom.pumpx2.pump.messages.request.authentication;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CentralChallengeV2RequestTest {
//    @Test
//    public void testTconnectAppFirstRequest() throws DecoderException {
//        CentralChallengeV2Request expected = new CentralChallengeV2Request(0, new byte[]{77, 8, 67, 93, -94, 105, 71, 53});
//
//        CentralChallengeRequest parsedReq = (CentralChallengeRequest) MessageTester.test(
//                "000010000a00004d08435da26947356d6f",
//                0,
//                1,
//                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
//                expected
//        );
//
//        assertEquals(expected.getAppInstanceId(), parsedReq.getAppInstanceId());
//        MessageTester.assertHexEquals(expected.getCentralChallenge(), parsedReq.getCentralChallenge());
//        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
//    }


    // ./android-2024-02-29-6char2.csv
    @Test
    public void test_167cargo_split() throws DecoderException {
        // empty cargo
        CentralChallengeV2Request expected = new CentralChallengeV2Request(
                new byte[]{
                        0,0,65,4,-26,-84,-43,124,-14,93,-23,-99,-103,-80,85,42,122,71,-10,-113,41,74,71,40,-42,-16,-13,3,34,-1,-12,-85,28,4,126,-6,-38,-90,-23,-104,12,40,-95,60,-60,-21,-121,6,78,-119,25,22,-19,39,-92,-120,31,93,-127,-102,58,108,-97,85,-50,76,-80,-128,65,4,15,-42,60,-7,-56,-106,46,52,43,-84,85,5,-123,-50,-111,-73,65,43,47,-53,48,27,-35,40,-68,42,118,37,-89,-106,27,-15,-56,-63,-104,65,-3,9,30,-110,2,-98,-101,-89,-123,-57,34,74,24,60,57,-114,51,107,-79,31,54,-68,-20,113,-24,61,-107,-115,32,64,78,-127,90,-54,89,28,18,-114,-101,-76,-105,81,-20,8,13,62,75,-41,63,-33,99,-43,-95,6,87,122,-86,102,-45,-89,-97
                }
        );

        CentralChallengeV2Request parsedReq = (CentralChallengeV2Request) MessageTester.test(
                "09002000a700004104e6acd57cf25de99d99b055",
                0,
                10,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected,
                "08002a7a47f68f294a4728d6f0f30322fff4ab1c",
                "0700047efadaa6e9980c28a13cc4eb87064e8919",
                "060016ed27a4881f5d819a3a6c9f55ce4cb08041",
                "0500040fd63cf9c8962e342bac550585ce91b741",
                "04002b2fcb301bdd28bc2a7625a7961bf1c8c198",
                "030041fd091e92029e9ba785c7224a183c398e33",
                "02006bb11f36bcec71e83d958d20404e815aca59",
                "01001c128e9bb49751ec080d3e4bd73fdf63d5a1",
                "000006577aaa66d3a79f4541"
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, parsedReq.getAppInstanceId());
        assertEquals(165, parsedReq.getCentralChallenge().length);
        MessageTester.assertHexEquals(new byte[]{
            65,4,-26,-84,-43,124,-14,93,-23,-99,-103,-80,85,42,122,71,-10,-113,41,74,71,40,-42,-16,-13,3,34,-1,-12,-85,28,4,126,-6,-38,-90,-23,-104,12,40,-95,60,-60,-21,-121,6,78,-119,25,22,-19,39,-92,-120,31,93,-127,-102,58,108,-97,85,-50,76,-80,-128,65,4,15,-42,60,-7,-56,-106,46,52,43,-84,85,5,-123,-50,-111,-73,65,43,47,-53,48,27,-35,40,-68,42,118,37,-89,-106,27,-15,-56,-63,-104,65,-3,9,30,-110,2,-98,-101,-89,-123,-57,34,74,24,60,57,-114,51,107,-79,31,54,-68,-20,113,-24,61,-107,-115,32,64,78,-127,90,-54,89,28,18,-114,-101,-76,-105,81,-20,8,13,62,75,-41,63,-33,99,-43,-95,6,87,122,-86,102,-45,-89,-97
        }, parsedReq.getCentralChallenge());
        // hex: 4104e6acd57cf25de99d99b0552a7a47f68f294a4728d6f0f30322fff4ab1c047efadaa6e9980c28a13cc4eb87064e891916ed27a4881f5d819a3a6c9f55ce4cb08041040fd63cf9c8962e342bac550585ce91b7412b2fcb301bdd28bc2a7625a7961bf1c8c19841fd091e92029e9ba785c7224a183c398e336bb11f36bcec71e83d958d20404e815aca591c128e9bb49751ec080d3e4bd73fdf63d5a106577aaa66d3a79f
    }
}
