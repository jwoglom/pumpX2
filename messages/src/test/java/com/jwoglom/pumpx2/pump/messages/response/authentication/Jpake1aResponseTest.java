package com.jwoglom.pumpx2.pump.messages.response.authentication;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class Jpake1aResponseTest {
    // ./android-2024-02-29-6char2.csv
    @Test
    public void test_167cargo_response_split() throws DecoderException {
        Jpake1aResponse expected = new Jpake1aResponse(
                new byte[]{
                        0,0,65,4,-127,57,-50,127,80,18,-30,-61,44,-117,-28,-93,-21,69,17,-7,-67,28,71,27,-19,15,28,-49,98,58,42,3,-103,-28,-9,-34,53,-32,12,42,-32,-40,-76,45,23,49,-125,-19,98,75,39,108,-81,-125,-69,104,-50,102,95,26,-54,-80,59,117,-128,86,-37,-54,65,4,122,14,4,-109,-100,0,-119,-28,77,-26,38,-114,0,24,-61,-112,-59,-5,29,77,-125,42,82,-3,103,-36,-48,3,-45,31,-43,118,-1,62,-73,-88,56,-48,-77,-119,-96,-46,84,79,-57,64,17,-102,-50,-39,49,-84,99,-123,-85,-116,-90,32,-32,117,109,23,-11,-5,32,-75,112,-22,-114,84,96,-52,69,-79,-73,51,-62,-19,-78,-68,50,-94,6,-15,-86,-71,86,-38,4,78,1,-70,27,-26,-48,-103,19
                }
        );

        Jpake1aResponse parsedReq = (Jpake1aResponse) MessageTester.test(
                "00002100a7000041048139ce7f5012e2c32c8be4a3eb4511f9bd1c471bed0f1ccf623a2a0399e4f7de35e00c2ae0d8b42d173183ed624b276caf83bb68ce665f1acab03b758056dbca41047a0e04939c0089e44de6268e0018c390c5fb1d4d832a52fd67dcd003d31fd576ff3eb7a838d0b389a0d2544fc740119aced931ac6385ab8ca620e0756d17f5fb20b570ea8e5460cc45b1b733c2edb2bc32a206f1aab956da044e01ba1be6d099132562",
                0,
                10,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    // ad-hoc
    @Test
    public void test_oneoff_response() throws DecoderException {
        // TandemPump: raw sendCommand to characteristic AUTHORIZATION: 09002000a701004104e6acd57cf25de99d99b055
        // TandemPump: raw sendCommand to characteristic AUTHORIZATION: 08002a7a47f68f294a4728d6f0f30322fff4ab1c
        // TandemPump: raw sendCommand to characteristic AUTHORIZATION: 0700047efadaa6e9980c28a13cc4eb87064e8919
        // TandemPump: raw sendCommand to characteristic AUTHORIZATION: 060016ed27a4881f5d819a3a6c9f55ce4cb08041
        // TandemPump: raw sendCommand to characteristic AUTHORIZATION: 0500040fd63cf9c8962e342bac550585ce91b741
        // TandemPump: raw sendCommand to characteristic AUTHORIZATION: 04002b2fcb301bdd28bc2a7625a7961bf1c8c198
        // TandemPump: raw sendCommand to characteristic AUTHORIZATION: 030041fd091e92029e9ba785c7224a183c398e33
        // TandemPump: raw sendCommand to characteristic AUTHORIZATION: 02006bb11f36bcec71e83d958d20404e815aca59
        // TandemPump: raw sendCommand to characteristic AUTHORIZATION: 01001c128e9bb49751ec080d3e4bd73fdf63d5a1
        // TandemPump: raw sendCommand to characteristic AUTHORIZATION: 000006577aaa66d3a79f1ecc
        // Received AUTHORIZATION response, txId 0: 00002100a70100410441e099e06fda4ba1ea6b6e727e4790fee1303018a97a8a1eed5093f2dab4cbced4ac1f392fbff14a89cc7b8ee06fdadd09d4b67222e1af69612ae3b727679ae94104c9dd154dd328e468cda64f39d6c5e5dfc6645fa60883cee41ca536d22fd6f5ddf12ed045c8eff2edf59f13249268cf403e19585711e8393872954632e896fbb820a8c41909ae5120fa17fc0716ca65dc5fb440e94b78ce9c82c997a0fc4f855d416119
        // central challenge length: 165

        Jpake1aResponse expected = new Jpake1aResponse(
                new byte[]{
                        1,0,65,4,65,-32,-103,-32,111,-38,75,-95,-22,107,110,114,126,71,-112,-2,-31,48,48,24,-87,122,-118,30,-19,80,-109,-14,-38,-76,-53,-50,-44,-84,31,57,47,-65,-15,74,-119,-52,123,-114,-32,111,-38,-35,9,-44,-74,114,34,-31,-81,105,97,42,-29,-73,39,103,-102,-23,65,4,-55,-35,21,77,-45,40,-28,104,-51,-90,79,57,-42,-59,-27,-33,-58,100,95,-90,8,-125,-50,-28,28,-91,54,-46,47,-42,-11,-35,-15,46,-48,69,-56,-17,-14,-19,-11,-97,19,36,-110,104,-49,64,62,25,88,87,17,-24,57,56,114,-107,70,50,-24,-106,-5,-72,32,-88,-60,25,9,-82,81,32,-6,23,-4,7,22,-54,101,-36,95,-76,64,-23,75,120,-50,-100,-126,-55,-105,-96,-4,79,-123,93,65
                }
        );

        Jpake1aResponse parsedReq = (Jpake1aResponse) MessageTester.test(
                "00002100a70100410441e099e06fda4ba1ea6b6e727e4790fee1303018a97a8a1eed5093f2dab4cbced4ac1f392fbff14a89cc7b8ee06fdadd09d4b67222e1af69612ae3b727679ae94104c9dd154dd328e468cda64f39d6c5e5dfc6645fa60883cee41ca536d22fd6f5ddf12ed045c8eff2edf59f13249268cf403e19585711e8393872954632e896fbb820a8c41909ae5120fa17fc0716ca65dc5fb440e94b78ce9c82c997a0fc4f855d416119",
                0,
                10,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        // todo: once split is identified, add tests for helpers
    }
}
