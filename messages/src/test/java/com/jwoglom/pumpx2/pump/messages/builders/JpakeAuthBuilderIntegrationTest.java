package com.jwoglom.pumpx2.pump.messages.builders;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.builders.crypto.Hkdf;
import com.jwoglom.pumpx2.pump.messages.builders.crypto.HmacSha256;
import com.jwoglom.pumpx2.pump.messages.builders.jpake.SecureRandomMock;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake1aRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake1bRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake2Request;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake3SessionKeyRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake4KeyConfirmationRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake1aResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake1bResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake2Response;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake3SessionKeyResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake4KeyConfirmationResponse;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

public class JpakeAuthBuilderIntegrationTest {
    @Test
    public void testWithPairingCode() throws DecoderException {
        SecureRandomMock rand = new SecureRandomMock(Hex.decodeHex(
                // writeRound1()
                "0bb1b515aebb230d9d16ec3702d92738dc9cd0819bf37787c456fbe39c6bcacd" +
                "2613f01a23a6e679b97ba9ec5d4be5f2dbf14bbbb9ea7b9f6651d96ef4e31b68" +
                "69b1439020883249904ceb5b4a15c40e34176df0ff8bc27029da4147665dcb59" +
                "10873ba39ebbeacb24ab2695fcc35e14bca6e512bf42f08b8dd838bde922f847" +
                // writeRound2()
                "455d8e81bee36509d0f77860a0641312" +
                "e14648731067823471832c9f3dc9e48275f1041b0332f5447cad007341a5e3c3" +
                // deriveSecret()
                "fc55f787a26b3f5619c891a3cd34907b" +
                // generateNonce() server
                "e734344901549417" +
                // generateNonce() client
                "998c182c9d70a375" +
                // generateNonce() server
                "ad08275f109e41b0"));
        String pairingCode = "passw0rd"; // "738006";
        JpakeAuthBuilder b = new JpakeAuthBuilder(pairingCode, null, rand);

        // (1a) CLIENT
        byte[] cliRound1 = Hex.decodeHex("4104e92f1a97685b86ea2e8a583724095e355955d1356942c2fa7a0da21f148690052607421562f9771fbcf70fdc33056b2f2596145d8c5cd7be986259e2918d9f554104854faebe27e2f81652f0e71b38410d704fc521965bd40005fa47de22d7de67c2ba301fe248f63b954891e5ba9237c4dace174b022dcc6d55cc977115e0e5e24a2062080ced0ce4c03ca7fb9c80e1374939956623bc951905ac6ed5c6a96ea647c34104580e59d4e2377620a0e2003a22cf5b603165676e48de7095c21f8c76afdef847bc976aa1f58ee050c757f9ccc2af19142a15714a27268886fc50ddf0f8b4573e41046b1d85ca2a6bf3e956269bac6529856ab73089e1522eba11b2b16f2e50908cd6ee7bb6b1f7ecefc424bebe177039e9e2c98da07c7f521388789d5bb37dc2830e209967d2ec8e533bb526645218a376bb3d318103e0aef96c300f30986ab3d0e027");
        Jpake1aRequest req1a = (Jpake1aRequest) b.nextRequest();
        assertEquals(165, req1a.getCentralChallenge().length);
        assertHexEquals(
                Arrays.copyOfRange(cliRound1, 0, 165),
                req1a.getCentralChallenge());

        assertArrayEquals(b.clientRound1, Hex.decodeHex("4104e92f1a97685b86ea2e8a583724095e355955d1356942c2fa7a0da21f148690052607421562f9771fbcf70fdc33056b2f2596145d8c5cd7be986259e2918d9f554104854faebe27e2f81652f0e71b38410d704fc521965bd40005fa47de22d7de67c2ba301fe248f63b954891e5ba9237c4dace174b022dcc6d55cc977115e0e5e24a2062080ced0ce4c03ca7fb9c80e1374939956623bc951905ac6ed5c6a96ea647c34104580e59d4e2377620a0e2003a22cf5b603165676e48de7095c21f8c76afdef847bc976aa1f58ee050c757f9ccc2af19142a15714a27268886fc50ddf0f8b4573e41046b1d85ca2a6bf3e956269bac6529856ab73089e1522eba11b2b16f2e50908cd6ee7bb6b1f7ecefc424bebe177039e9e2c98da07c7f521388789d5bb37dc2830e209967d2ec8e533bb526645218a376bb3d318103e0aef96c300f30986ab3d0e027"));

        // (1a) SERVER
        byte[] servRound1 = Hex.decodeHex("4104e7c5d0684bb95148e935d86377befafeb346036808c8ce70f295ba471bafffeef40c462dd49716f13d2ff82f4365d4429d9280caf443dcd0ec79161b0d9ce4cd4104a3b138f08ac33bd583035bec94575073245eb08d7b3aebfb948d139fb8504b67167a9c6ff8dfca50787f5a3e31ed8ddb4af5d717456aa6f3ac8ddbbc9d1a2cba20459cdfbcaae4e8b1017df914f01848eb40149188a3cfc2a988e03d794df6ba1e41040f825be5af5c808c38e0e8878751b07d4bddd372df34a1c64ffeb5a1e8b97fad413c18bd62a55b02fad86b1cef68b6cce71daf3c932f3bf15c5e57dca9dd15b34104d057ea7f55f38a200966ff4ac8fddc1fd8a6021e6ee2f50739e61c6bfca0bcb28b92b964ca24f846327695d654ba1f363cbf8b9ba67b12c61d4c58e34e2176f4201e77da3bb44e301aac88dc3749ff33d1f78ce1a73afc7de414e584bca28c37ab");
        Jpake1aResponse res1a = new Jpake1aResponse(0, Arrays.copyOfRange(servRound1, 0, 165));
        b.processResponse(res1a);

        // (1b) CLIENT
        Jpake1bRequest req1b = (Jpake1bRequest) b.nextRequest();
        assertEquals(165, req1b.getCentralChallenge().length);
        assertHexEquals(
                Arrays.copyOfRange(cliRound1, 165, 330),
                req1b.getCentralChallenge());

        assertHexEquals(cliRound1, b.clientRound1);

        // (1b) SERVER
        Jpake1bResponse res1b = new Jpake1bResponse(0, Arrays.copyOfRange(servRound1, 165, 330));
        b.processResponse(res1b);

        assertHexEquals(servRound1, b.serverRound1);

        // (2) CLIENT
        byte[] cliRound2 = Hex.decodeHex("41047a06fd054b56eb2ddc9f074a64d0fd12747006f1c2ea44bc86ec2394672b063fa6234f4d160f0020b4c322382b96176d9a25c4c41947915ec313006ee418385b41040fbeb1bc502d7017aa8d15e20cc43f89c46270c4a3a101cb5cb7ea9b2075ad10753dd1a327f9bdf91121d5da5c8c3e1827fcd21951f9dd944faffdf6688fcf11207e9c7aa10d4456096d53f77803765051de82196176d9c9513ff36821a64862cf");
        Jpake2Request req2 = (Jpake2Request) b.nextRequest();
        assertEquals(165, req2.getCentralChallenge().length);
        assertHexEquals(cliRound2, req2.getCentralChallenge());

        assertHexEquals(cliRound2, b.clientRound2);

        // (2) SERVER
        byte[] servRound2 = Hex.decodeHex("0300174104a674d165d4b727d76954b810d90c8c549b38bacf9b322d32f8f5ed5d2bd00b0a93326843b9955024b84eb22c320d36350caa57ad4a0744e322d6dc98cec8635b4104474737716644c4813db988da1de8284ac59cc18f9e172da43db80e9e4ef70eae998857526e53a99156d4e137c1cdd3ab1033d343dda08c581085a71fc6478c4120c52706456c50213629b91a003078544dc6da201caed7d4f9a7189fa1e537259f");
        Jpake2Response res2 = new Jpake2Response(0, servRound2);
        b.processResponse(res2);


        assertHexEquals(servRound2, b.serverRound2);

        // (3) CLIENT
        Jpake3SessionKeyRequest req3 = (Jpake3SessionKeyRequest) b.nextRequest();
        assertEquals(req3.getChallengeParam(), 0);

        byte[] secret = Hex.decodeHex("45d66d65aedfd39ce50be0eacca491ff183b7e1c22bf722b8dfb20408e0c78d4");
        assertHexEquals(secret, b.derivedSecret);

        // (3) SERVER
        byte[] nonce = b.generateNonce();
        assertHexEquals(nonce, Hex.decodeHex("e734344901549417"));
        Jpake3SessionKeyResponse res3 = new Jpake3SessionKeyResponse(0, nonce, Jpake3SessionKeyResponse.RESERVED);

        b.processResponse(res3);
        assertHexEquals(nonce, b.serverNonce3);

        // (4) CLIENT
        Jpake4KeyConfirmationRequest req4 = (Jpake4KeyConfirmationRequest) b.nextRequest();
        byte[] clientHashDigest = HmacSha256.hmacSha256(req4.getNonce(), Hkdf.build(b.serverNonce3, b.derivedSecret));
        assertEquals(32, clientHashDigest.length);
        assertHexEquals(req4.getHashDigest(), clientHashDigest);
        byte[] expectedHashDigest = Hex.decodeHex("78277ee13aff3fbe0587a6666445eb329b03be0cacfbc7da6f6213765f31371b");
        assertHexEquals(clientHashDigest, expectedHashDigest);

        assertHexEquals(req4.getNonce(), b.clientNonce4);

        // (4) SERVER
        byte[] serverNonce = b.generateNonce();
        assertHexEquals(serverNonce, Hex.decodeHex("ad08275f109e41b0"));
        byte[] serverHashDigest = HmacSha256.hmacSha256(serverNonce, Hkdf.build(b.serverNonce3, b.derivedSecret));
        assertEquals(32, serverHashDigest.length);

        byte[] expectedServerHashDigest = Hex.decodeHex("6e7a179c5a601572a2b91251e577454c8b32ebeafae5bc87209cced02fa7b358");
        assertHexEquals(serverHashDigest, expectedServerHashDigest);
        Jpake4KeyConfirmationResponse res4 = new Jpake4KeyConfirmationResponse(
                0,
                serverNonce,
                Jpake4KeyConfirmationResponse.RESERVED,
                serverHashDigest);
        b.processResponse(res4);

        assertNull(b.nextRequest());
        assertEquals(JpakeAuthBuilder.JpakeStep.COMPLETE, b.step);
        byte[] derivedSecret = Hex.decodeHex("45d66d65aedfd39ce50be0eacca491ff183b7e1c22bf722b8dfb20408e0c78d4");
        assertHexEquals(derivedSecret, b.getDerivedSecret());
        assertTrue(b.done());
    }

    @Test
    public void testWithDerivedSecret() throws DecoderException {
        SecureRandomMock rand = new SecureRandomMock(Hex.decodeHex(
                // generateNonce() server
                "e734344901549417" +
                // generateNonce() client
                "998c182c9d70a375" +
                // generateNonce() server
                "ad08275f109e41b0"));

        String pairingCode = "passw0rd"; // "738006";
        byte[] derivedSecret = Hex.decodeHex("45d66d65aedfd39ce50be0eacca491ff183b7e1c22bf722b8dfb20408e0c78d4");
        JpakeAuthBuilder b = new JpakeAuthBuilder(pairingCode, derivedSecret, rand);


        // (3) CLIENT
        Jpake3SessionKeyRequest req3 = (Jpake3SessionKeyRequest) b.nextRequest();
        assertEquals(req3.getChallengeParam(), 0);

        byte[] secret = Hex.decodeHex("45d66d65aedfd39ce50be0eacca491ff183b7e1c22bf722b8dfb20408e0c78d4");
        assertHexEquals(secret, b.derivedSecret);

        // (3) SERVER
        byte[] nonce = b.generateNonce();
        assertHexEquals(nonce, Hex.decodeHex("e734344901549417"));
        Jpake3SessionKeyResponse res3 = new Jpake3SessionKeyResponse(0, nonce, Jpake3SessionKeyResponse.RESERVED);

        b.processResponse(res3);
        assertHexEquals(nonce, b.serverNonce3);


        // (4) CLIENT
        Jpake4KeyConfirmationRequest req4 = (Jpake4KeyConfirmationRequest) b.nextRequest();
        byte[] clientHashDigest = HmacSha256.hmacSha256(req4.getNonce(), Hkdf.build(b.serverNonce3, b.derivedSecret));
        assertEquals(32, clientHashDigest.length);
        assertHexEquals(req4.getHashDigest(), clientHashDigest);
        byte[] expectedHashDigest = Hex.decodeHex("78277ee13aff3fbe0587a6666445eb329b03be0cacfbc7da6f6213765f31371b");
        assertHexEquals(clientHashDigest, expectedHashDigest);

        assertHexEquals(req4.getNonce(), b.clientNonce4);

        // (4) SERVER
        byte[] serverNonce = b.generateNonce();
        assertHexEquals(serverNonce, Hex.decodeHex("ad08275f109e41b0"));
        byte[] serverHashDigest = HmacSha256.hmacSha256(serverNonce, Hkdf.build(b.serverNonce3, b.derivedSecret));
        assertEquals(32, serverHashDigest.length);

        byte[] expectedServerHashDigest = Hex.decodeHex("6e7a179c5a601572a2b91251e577454c8b32ebeafae5bc87209cced02fa7b358");
        assertHexEquals(serverHashDigest, expectedServerHashDigest);
        Jpake4KeyConfirmationResponse res4 = new Jpake4KeyConfirmationResponse(
                0,
                serverNonce,
                Jpake4KeyConfirmationResponse.RESERVED,
                serverHashDigest);
        b.processResponse(res4);

        assertNull(b.nextRequest());
        assertEquals(JpakeAuthBuilder.JpakeStep.COMPLETE, b.step);
        assertTrue(b.done());
    }
}
