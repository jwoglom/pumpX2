package com.jwoglom.pumpx2.pump.messages.request.authentication;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpChallengeV2RequestTest {
    // ./android-2024-02-29-6char2.csv

    // CentralChallengeV2Request:
    //1759	102.330431	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	09002000a700004104e6acd57cf25de99d99b055	Sent Write Command, Handle: 0x0022 (Unknown)
    //1760	102.333404	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	08002a7a47f68f294a4728d6f0f30322fff4ab1c	Sent Write Command, Handle: 0x0022 (Unknown)
    //1761	102.335841	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0700047efadaa6e9980c28a13cc4eb87064e8919	Sent Write Command, Handle: 0x0022 (Unknown)
    //1762	102.338132	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	060016ed27a4881f5d819a3a6c9f55ce4cb08041	Sent Write Command, Handle: 0x0022 (Unknown)
    //1763	102.344106	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0500040fd63cf9c8962e342bac550585ce91b741	Sent Write Command, Handle: 0x0022 (Unknown)
    //1764	102.347508	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	04002b2fcb301bdd28bc2a7625a7961bf1c8c198	Sent Write Command, Handle: 0x0022 (Unknown)
    //1765	102.349820	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	030041fd091e92029e9ba785c7224a183c398e33	Sent Write Command, Handle: 0x0022 (Unknown)
    //1766	102.351939	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	02006bb11f36bcec71e83d958d20404e815aca59	Sent Write Command, Handle: 0x0022 (Unknown)
    //1767	102.353271	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	01001c128e9bb49751ec080d3e4bd73fdf63d5a1	Sent Write Command, Handle: 0x0022 (Unknown)
    //1768	102.360369	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	24	000006577aaa66d3a79f4541	Sent Write Command, Handle: 0x0022 (Unknown)
    // CentralChallengeV2Response:
    // 1772	102.510261	c2:48:14:3a:a4:c8 (tslim X2 ***941)	Google_aa:a4:cd (James' Pixel 7a)	ATT	186	00002100a7000041048139ce7f5012e2c32c8be4a3eb4511f9bd1c471bed0f1ccf623a2a0399e4f7de35e00c2ae0d8b42d173183ed624b276caf83bb68ce665f1acab03b758056dbca41047a0e04939c0089e44de6268e0018c390c5fb1d4d832a52fd67dcd003d31fd576ff3eb7a838d0b389a0d2544fc740119aced931ac6385ab8ca620e0756d17f5fb20b570ea8e5460cc45b1b733c2edb2bc32a206f1aab956da044e01ba1be6d099132562	Rcvd Handle Value Notification, Handle: 0x0022 (Unknown)
    // PumpChallengeV2Request:
    // 1773	102.518600	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	09012201a700004104faf1c1c7737041a88872ba	Sent Write Command, Handle: 0x0022 (Unknown)
    //1774	102.520348	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0801435c79fe67b14fd793f452b4e1a41cf4e856	Sent Write Command, Handle: 0x0022 (Unknown)
    //1775	102.521838	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	07019ed1b67c9ab0523d97f79a536ab498d23d0d	Sent Write Command, Handle: 0x0022 (Unknown)
    //1776	102.523399	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0601941443b319402f9406393c1c800c203cac41	Sent Write Command, Handle: 0x0022 (Unknown)
    //1777	102.524779	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0501042dd19aad7f25eabd064b6f2ee474ac900f	Sent Write Command, Handle: 0x0022 (Unknown)
    //1778	102.525945	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0401d61550aef781eca29b4e9fdb921a1ecd395e	Sent Write Command, Handle: 0x0022 (Unknown)
    //1779	102.527333	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	030188cc0ee6f09c961d49e3e31d22cb178ea63c	Sent Write Command, Handle: 0x0022 (Unknown)
    //1780	102.529787	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0201e560ebe71127521b6f7d3320303b8172c67d	Sent Write Command, Handle: 0x0022 (Unknown)
    //1781	102.535026	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0101855dbdeeac9a32f1f067ea4924b957015630	Sent Write Command, Handle: 0x0022 (Unknown)
    //1782	102.540712	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	24	000121874c2f78785e6afd38	Sent Write Command, Handle: 0x0022 (Unknown)
    // PumpChallengeV2Response:
    //1787	102.842949	c2:48:14:3a:a4:c8 (tslim X2 ***941)	Google_aa:a4:cd (James' Pixel 7a)	ATT	186	00012301a700004104dc82c0b7f60e601ebed41ebafac79dac6b23055d6c2949e3bd7643acd951c400ca60513dbff125da5238e0a7eee27ff4533afded0725ad2804987c90646ade0f41048177dda93af133fcfcc3a78408af82370d76af3ecfe78bc16c732310ed00b188ae0b4c2769876ac29d6c65a205c96dd518e3166aa57d61bca1a6756aabbe4f6920c472ac523abdd69e678149c128daa073861c6c9371f04254d158b6481f2226ba3927	Rcvd Handle Value Notification, Handle: 0x0022 (Unknown)
    // ThirdChallengeV2Request:
    //1788	102.861444	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	09022402a700004104cf87b9389904510a1dc60a	Sent Write Command, Handle: 0x0022 (Unknown)
    //1789	102.862735	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	080201b0bcfcfbcd15787aa9af7fc5ec130d29b4	Sent Write Command, Handle: 0x0022 (Unknown)
    //1790	102.863998	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	070222799cc0faeec10f0d1800052c882a4100be	Sent Write Command, Handle: 0x0022 (Unknown)
    //1791	102.865275	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0602cee73d5c98c31d264428026f23994ecfdf41	Sent Write Command, Handle: 0x0022 (Unknown)
    //1792	102.867117	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0502040f3dbda4ba90c341c891376502823e405a	Sent Write Command, Handle: 0x0022 (Unknown)
    //1793	102.869067	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	0402df32dc3c7039d7399e1b4953a435d056fd2a	Sent Write Command, Handle: 0x0022 (Unknown)
    //1794	102.870815	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	03021ed2a834f8f40c6215f503dd470c6862d63e	Sent Write Command, Handle: 0x0022 (Unknown)
    //1795	102.872200	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	02022fa47c52b90e77e6cd725920f12b8b1ffadb	Sent Write Command, Handle: 0x0022 (Unknown)
    //1796	102.873266	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	01020c65b53eba740aa751f3c59912e19a5f2e66	Sent Write Command, Handle: 0x0022 (Unknown)
    //1797	102.874216	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	24	000222619797fe770c0b4106	Sent Write Command, Handle: 0x0022 (Unknown)
    // ThirdChallengeV2Response:
    //1805	103.080736	c2:48:14:3a:a4:c8 (tslim X2 ***941)	Google_aa:a4:cd (James' Pixel 7a)	ATT	141	00022502aa00000300174104634f4cfde021623a609c98311ae7508b41126542de029b76b1781de8f3db6e723950a8e64715c97cf44d80a6976b91ab846cdbf732bf160c1c0223f0a674a4734104732ff948fed2dbb06806502a7ae7ca0afdf1991eab79c866ff53e9d23bd4d1886f3cb55663b7979947f6a96d52511ac843b9d28b20805f263365bc50d1b93d7620bec12ade679a6b482ba5bfbe973e91dcdcd87bc3ab04090803db2945cca39e4c9be8	Rcvd Handle Value Notification, Handle: 0x0022 (Unknown)
    // FourthChallengeV2Request:
    // 1806	103.102521	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	21	000326030200008121	Sent Write Command, Handle: 0x0022 (Unknown)
    // FourthChallengeV2Response:
    // 1808	103.174065	c2:48:14:3a:a4:c8 (tslim X2 ***941)	Google_aa:a4:cd (James' Pixel 7a)	ATT	37	0003270312000002e91fff505adb4f0000000000000000ba54	Rcvd Handle Value Notification, Handle: 0x0022 (Unknown)
    // FifthChallengeV2Request:
    //1809	103.184105	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	03042804320000571ad034741c777d0000000000	Sent Write Command, Handle: 0x0022 (Unknown)
    //1810	103.187141	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	02040000002fece0828a91f7372a47cb7bf597a2	Sent Write Command, Handle: 0x0022 (Unknown)
    //1811	103.188685	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	32	010496961f66f0f45c4b7b76e9aeaf8f176f6d44	Sent Write Command, Handle: 0x0022 (Unknown)
    //1812	103.190121	Google_aa:a4:cd (James' Pixel 7a)	c2:48:14:3a:a4:c8 (tslim X2 ***941)	ATT	15	00046e	Sent Write Command, Handle: 0x0022 (Unknown)
    // FifthChallengeV2Response:
    //1814	103.269215	c2:48:14:3a:a4:c8 (tslim X2 ***941)	Google_aa:a4:cd (James' Pixel 7a)	ATT	69	00042904320000e3fd32509dfacf470000000000000000d0410856c350ce6b9756e03810c6f99a4a0743160a1fdb0973db2f90bdc9a96ad742	Rcvd Handle Value Notification, Handle: 0x0022 (Unknown)
    @Test
    public void test_167cargo_pumpchallenge_split() throws DecoderException {

        PumpChallengeV2Request expected = new PumpChallengeV2Request(
                0,
                new byte[]{
                    65,4,-6,-15,-63,-57,115,112,65,-88,-120,114,-70,67,92,121,-2,103,-79,79,-41,-109,-12,82,-76,-31,-92,28,-12,-24,86,-98,-47,-74,124,-102,-80,82,61,-105,-9,-102,83,106,-76,-104,-46,61,13,-108,20,67,-77,25,64,47,-108,6,57,60,28,-128,12,32,60,-84,65,4,45,-47,-102,-83,127,37,-22,-67,6,75,111,46,-28,116,-84,-112,15,-42,21,80,-82,-9,-127,-20,-94,-101,78,-97,-37,-110,26,30,-51,57,94,-120,-52,14,-26,-16,-100,-106,29,73,-29,-29,29,34,-53,23,-114,-90,60,-27,96,-21,-25,17,39,82,27,111,125,51,32,48,59,-127,114,-58,125,-123,93,-67,-18,-84,-102,50,-15,-16,103,-22,73,36,-71,87,1,86,48,33,-121,76,47,120,120,94,106
                }
        );

        PumpChallengeV2Request parsedReq = (PumpChallengeV2Request) MessageTester.test(
                "09012201a700004104faf1c1c7737041a88872ba",
                1,
                10,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected,
                "0801435c79fe67b14fd793f452b4e1a41cf4e856",
                "07019ed1b67c9ab0523d97f79a536ab498d23d0d",
                "0601941443b319402f9406393c1c800c203cac41",
                "0501042dd19aad7f25eabd064b6f2ee474ac900f",
                "0401d61550aef781eca29b4e9fdb921a1ecd395e",
                "030188cc0ee6f09c961d49e3e31d22cb178ea63c",
                "0201e560ebe71127521b6f7d3320303b8172c67d",
                "0101855dbdeeac9a32f1f067ea4924b957015630",
                "000121874c2f78785e6afd38"
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, parsedReq.getAppInstanceId());
        assertEquals(165, parsedReq.getCentralChallenge().length);
        MessageTester.assertHexEquals(new byte[]{
                65,4,-6,-15,-63,-57,115,112,65,-88,-120,114,-70,67,92,121,-2,103,-79,79,-41,-109,-12,82,-76,-31,-92,28,-12,-24,86,-98,-47,-74,124,-102,-80,82,61,-105,-9,-102,83,106,-76,-104,-46,61,13,-108,20,67,-77,25,64,47,-108,6,57,60,28,-128,12,32,60,-84,65,4,45,-47,-102,-83,127,37,-22,-67,6,75,111,46,-28,116,-84,-112,15,-42,21,80,-82,-9,-127,-20,-94,-101,78,-97,-37,-110,26,30,-51,57,94,-120,-52,14,-26,-16,-100,-106,29,73,-29,-29,29,34,-53,23,-114,-90,60,-27,96,-21,-25,17,39,82,27,111,125,51,32,48,59,-127,114,-58,125,-123,93,-67,-18,-84,-102,50,-15,-16,103,-22,73,36,-71,87,1,86,48,33,-121,76,47,120,120,94,106
        }, parsedReq.getCentralChallenge());

    }
}
