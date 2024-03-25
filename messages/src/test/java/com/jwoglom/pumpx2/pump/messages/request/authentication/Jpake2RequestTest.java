package com.jwoglom.pumpx2.pump.messages.request.authentication;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class Jpake2RequestTest {
    // ./android-2024-02-29-6char2.csv
    @Test
    public void test_167cargo_thirdchallenge_split() throws DecoderException {
        Jpake2Request expected = new Jpake2Request(
                0,
                new byte[]{
                        65,4,-49,-121,-71,56,-103,4,81,10,29,-58,10,1,-80,-68,-4,-5,-51,21,120,122,-87,-81,127,-59,-20,19,13,41,-76,34,121,-100,-64,-6,-18,-63,15,13,24,0,5,44,-120,42,65,0,-66,-50,-25,61,92,-104,-61,29,38,68,40,2,111,35,-103,78,-49,-33,65,4,15,61,-67,-92,-70,-112,-61,65,-56,-111,55,101,2,-126,62,64,90,-33,50,-36,60,112,57,-41,57,-98,27,73,83,-92,53,-48,86,-3,42,30,-46,-88,52,-8,-12,12,98,21,-11,3,-35,71,12,104,98,-42,62,47,-92,124,82,-71,14,119,-26,-51,114,89,32,-15,43,-117,31,-6,-37,12,101,-75,62,-70,116,10,-89,81,-13,-59,-103,18,-31,-102,95,46,102,34,97,-105,-105,-2,119,12,11
                }
        );

        Jpake2Request parsedReq = (Jpake2Request) MessageTester.test(
                "09022402a700004104cf87b9389904510a1dc60a",
                2,
                10,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected,
                "080201b0bcfcfbcd15787aa9af7fc5ec130d29b4",
                "070222799cc0faeec10f0d1800052c882a4100be",
                "0602cee73d5c98c31d264428026f23994ecfdf41",
                "0502040f3dbda4ba90c341c891376502823e405a",
                "0402df32dc3c7039d7399e1b4953a435d056fd2a",
                "03021ed2a834f8f40c6215f503dd470c6862d63e",
                "02022fa47c52b90e77e6cd725920f12b8b1ffadb",
                "01020c65b53eba740aa751f3c59912e19a5f2e66",
                "000222619797fe770c0b4106"
        );

        MessageTester.assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, parsedReq.getAppInstanceId());
        assertEquals(165, parsedReq.getCentralChallenge().length);
        MessageTester.assertHexEquals(expected.getCentralChallenge(), parsedReq.getCentralChallenge());

    }
}
