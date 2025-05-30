package com.jwoglom.pumpx2.pump.messages.builders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.commons.codec.digest.HmacUtils.hmacSha256;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.builders.crypto.AllZeroSecureRandom;
import com.jwoglom.pumpx2.pump.messages.builders.crypto.Hkdf;
import com.jwoglom.pumpx2.pump.messages.builders.crypto.HmacSha256;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
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
import com.jwoglom.pumpx2.shared.L;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.particle.crypto.EcJpake;

public class JpakeAuthBuilder {
    private static final Logger log = LoggerFactory.getLogger(JpakeAuthBuilder.class);

    private String pairingCode;
    private List<Message> sentMessages;
    private List<Message> receivedMessages;
    byte[] clientRound1;
    byte[] serverRound1;
    byte[] clientRound2;
    byte[] serverRound2;
    byte[] derivedSecret;
    byte[] serverNonce3;
    byte[] clientNonce4;
    byte[] serverHashDigest4;
    byte[] serverNonce4;
    private EcJpake cli;
    private SecureRandom rand;

    JpakeStep step;

    public JpakeAuthBuilder(String pairingCode, JpakeStep step, byte[] clientRound1, byte[] serverRound1, byte[] clientRound2, byte[] serverRound2, byte[] derivedSecret, SecureRandom rand) {
        this.pairingCode = pairingCode;
        this.cli = new EcJpake(EcJpake.Role.CLIENT, pairingCodeToBytes(pairingCode), rand);
        this.sentMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();

        this.step = step;
        this.clientRound1 = clientRound1;
        this.serverRound1 = serverRound1;
        this.clientRound2 = clientRound2;
        this.serverRound2 = serverRound2;
        this.derivedSecret = derivedSecret;
        this.rand = rand;
    }

    public JpakeAuthBuilder(String pairingCode) {
        this(pairingCode, decideInitialStep(null), null, null, null, null, null, new SecureRandom());
    }

    public JpakeAuthBuilder(String pairingCode, byte[] derivedSecret) {
        this(pairingCode, decideInitialStep(derivedSecret), null, null, null, null, derivedSecret, new SecureRandom());
    }

    public JpakeAuthBuilder(String pairingCode, byte[] derivedSecret, SecureRandom random) {
        this(pairingCode, decideInitialStep(derivedSecret), null, null, null, null, derivedSecret, random);
    }

    static JpakeStep decideInitialStep(byte[] derivedSecret) {
        if (derivedSecret == null || derivedSecret.length == 0) {
            return JpakeStep.BOOTSTRAP_INITIAL;
        } else {
            return JpakeStep.CONFIRM_INITIAL;
        }
    }

    static byte[] pairingCodeToBytes(String pairingCode) {
        //return pairingCode.getBytes(StandardCharsets.UTF_8);
        byte[] ret = new byte[pairingCode.length()]; // should always be 6
        for (int i=0; i<pairingCode.length(); i++) {
            ret[i] = charCode(pairingCode.charAt(i));
        }
        return ret;
    }

    static byte charCode(char c) {
        if (c == '0') return 48;
        if (c == '1') return 49;
        if (c == '2') return 50;
        if (c == '3') return 51;
        if (c == '4') return 52;
        if (c == '5') return 53;
        if (c == '6') return 54;
        if (c == '7') return 55;
        if (c == '8') return 56;
        if (c == '9') return 57;
        return -1;
    }

    private static JpakeAuthBuilder INSTANCE = null;
    public static JpakeAuthBuilder initializeWithPairingCode(String pairingCode) {
        if (INSTANCE == null || !INSTANCE.pairingCode.equals(pairingCode)) {
            INSTANCE = new JpakeAuthBuilder(pairingCode, null);
        }
        return INSTANCE;
    }

    public static JpakeAuthBuilder initializeWithDerivedSecret(String pairingCode, byte[] derivedSecret) {
        INSTANCE = new JpakeAuthBuilder(pairingCode, derivedSecret);
        return INSTANCE;
    }

    public static JpakeAuthBuilder getInstance() {
        if (INSTANCE == null) {
            throw new IllegalArgumentException("JPAKE auth session does not exist");
        }
        return INSTANCE;
    }

    public static void clearInstance() {
        INSTANCE = null;
    }

    public Message nextRequest() {
        Message request;
        if (step == JpakeStep.BOOTSTRAP_INITIAL) {
            this.clientRound1 = this.cli.getRound1();
            byte[] challenge = Arrays.copyOfRange(this.clientRound1, 0, 165);

            log.debug("Req1a: " + Hex.encodeHexString(challenge));
            request = new Jpake1aRequest(0, challenge);

            step = JpakeStep.ROUND_1A_SENT;
        } else if (step == JpakeStep.ROUND_1A_RECEIVED) {
            byte[] challenge = Arrays.copyOfRange(this.clientRound1, 165, 330);

            log.debug("Req1b: " + Hex.encodeHexString(challenge));
            request = new Jpake1bRequest(0, challenge);

            step = JpakeStep.ROUND_1B_SENT;
        } else if (step == JpakeStep.ROUND_1B_RECEIVED) {
            this.clientRound2 = this.cli.getRound2();
            byte[] challenge = Arrays.copyOfRange(this.clientRound2, 0, 165);
            log.debug("Req2: " + Hex.encodeHexString(challenge));
            request = new Jpake2Request(0, challenge);

            step = JpakeStep.ROUND_2_SENT;
        } else if (step == JpakeStep.ROUND_2_RECEIVED) {
            request = new Jpake3SessionKeyRequest(0);

            this.derivedSecret = this.cli.deriveSecret();
            log.debug("Req3 DerivedSecret=" + Hex.encodeHexString(derivedSecret));

            step = JpakeStep.CONFIRM_3_SENT;
        } else if (step == JpakeStep.CONFIRM_INITIAL) {
            request = new Jpake3SessionKeyRequest(0);

            step = JpakeStep.CONFIRM_3_SENT;
        } else if (step == JpakeStep.CONFIRM_3_RECEIVED) {
            this.clientNonce4 = generateNonce();
            byte[] hashDigest3 = HmacSha256.hmacSha256(clientNonce4, Hkdf.build(serverNonce3, derivedSecret));

            log.debug("Req4: clientNonce4=" + Hex.encodeHexString(clientNonce4));
            log.debug("Req4: derivedSecret=" + Hex.encodeHexString(derivedSecret));
            log.debug("Req4: serverNonce3=" + Hex.encodeHexString(serverNonce3));
            log.debug("Req4: hashDigest3=" + Hex.encodeHexString(hashDigest3));
            request = new Jpake4KeyConfirmationRequest(0,
                    clientNonce4,
                    Jpake4KeyConfirmationRequest.RESERVED,
                    hashDigest3
            );

            step = JpakeStep.CONFIRM_4_SENT;
        } else if (step == JpakeStep.CONFIRM_4_RECEIVED) {
            byte[] hashDigest4 = HmacSha256.hmacSha256(serverNonce4, Hkdf.build(serverNonce3, derivedSecret));
            if (Hex.encodeHexString(this.serverHashDigest4).equals(Hex.encodeHexString(hashDigest4))) {
                log.info("JpakeAuthBuilder HMAC SECRET VALIDATES");
                step = JpakeStep.COMPLETE;
            } else {
                log.warn("JpakeAuthBuilder HMAC SECRET DOES NOT VALIDATE hashDigest=" + Hex.encodeHexString(hashDigest4));
                log.warn("serverNonce4=" + Hex.encodeHexString(this.serverNonce4));
                log.warn("derivedSecret=" + Hex.encodeHexString(this.derivedSecret));
                log.warn("serverHashDigest4=" + Hex.encodeHexString(this.serverHashDigest4));
                step = JpakeStep.INVALID;
            }

            return null;
        } else {
            return null;
        }

        this.sentMessages.add(request);
        return request;
    }

    public void processResponse(Message response) {
        this.receivedMessages.add(response);
        if (response instanceof Jpake1aResponse) {
            Jpake1aResponse m = (Jpake1aResponse) response;
            log.debug("Res1a: " + Hex.encodeHexString(m.getCentralChallengeHash()));
            this.serverRound1 = m.getCentralChallengeHash();

            step = JpakeStep.ROUND_1A_RECEIVED;
        } else if (response instanceof Jpake1bResponse) {
            Jpake1bResponse m = (Jpake1bResponse) response;
            log.debug("Res1b: " + Hex.encodeHexString(m.getCentralChallengeHash()));
            byte[] fullServerRound1 = Bytes.combine(this.serverRound1, m.getCentralChallengeHash());
            log.debug("FULL_ROUND_1_RESPONSE: " + Hex.encodeHexString(fullServerRound1));
            this.serverRound1 = fullServerRound1;

            this.cli.readRound1(fullServerRound1);
            step = JpakeStep.ROUND_1B_RECEIVED;
        } else if (response instanceof Jpake2Response) {
            Jpake2Response m = (Jpake2Response) response;
            log.debug("Res2: " + Hex.encodeHexString(m.getCentralChallengeHash()));
            this.serverRound2 = m.getCentralChallengeHash();
            log.debug("FULL_ROUND_2_RESPONSE: " + Hex.encodeHexString(this.serverRound2));

            this.cli.readRound2(this.serverRound2);
            step = JpakeStep.ROUND_2_RECEIVED;
        } else if (response instanceof Jpake3SessionKeyResponse) {
            Jpake3SessionKeyResponse m = (Jpake3SessionKeyResponse) response;
            log.debug("Res3: nonce=" + Hex.encodeHexString(m.getDeviceKeyNonce()));
            log.debug("Res3: reserved=" + Hex.encodeHexString(m.getDeviceKeyReserved()));

            this.serverNonce3 = m.getDeviceKeyNonce();
            step = JpakeStep.CONFIRM_3_RECEIVED;
        } else if (response instanceof Jpake4KeyConfirmationResponse) {
            Jpake4KeyConfirmationResponse m = (Jpake4KeyConfirmationResponse) response;
            log.debug("JpakeAuthBuilder Res4: nonce=" + Hex.encodeHexString(m.getHashDigest()) + " hashDigest=" + Hex.encodeHexString(m.getNonce()));

            this.serverNonce4 = m.getNonce();
            this.serverHashDigest4 = m.getHashDigest();
            step = JpakeStep.CONFIRM_4_RECEIVED;
        }
    }

    byte[] generateNonce() {
        byte[] nonce = new byte[8];
        this.rand.nextBytes(nonce);
        return nonce;
    }

    public enum JpakeStep {
        BOOTSTRAP_INITIAL,
        ROUND_1A_SENT,
        ROUND_1A_RECEIVED,
        ROUND_1B_SENT,
        ROUND_1B_RECEIVED,
        ROUND_2_SENT,
        ROUND_2_RECEIVED,
        CONFIRM_INITIAL,
        CONFIRM_3_SENT,
        CONFIRM_3_RECEIVED,
        CONFIRM_4_SENT,
        CONFIRM_4_RECEIVED,
        COMPLETE,
        INVALID
    }

    public boolean done() {
        return step == JpakeStep.COMPLETE;
    }

    public boolean invalid() {
        return step == JpakeStep.INVALID;
    }

    public byte[] getDerivedSecret() {
        return this.derivedSecret;
    }

    public byte[] getServerNonce() {
        return this.serverNonce3;
    }
}
