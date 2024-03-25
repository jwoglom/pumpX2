package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake1aRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake1bRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake2Request;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake1aResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake1bResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake2Response;
import com.jwoglom.pumpx2.shared.Hex;
import com.jwoglom.pumpx2.shared.L;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.particle.crypto.EcJpake;

public class JpakeAuthBuilder {
    private static String TAG = "JPAKE";

    private String pairingCode;
    private List<Message> sentMessages;
    private List<Message> receivedMessages;
    private byte[] clientRound1;
    private byte[] serverRound1;
    private byte[] clientRound2;
    private byte[] serverRound2;
    private EcJpake cli;

    private JpakeStep step;

    public JpakeAuthBuilder(String pairingCode, JpakeStep step, byte[] clientRound1, byte[] serverRound1, byte[] clientRound2, byte[] serverRound2) {
        this.pairingCode = pairingCode;
        this.cli = new EcJpake(EcJpake.Role.CLIENT, pairingCode.getBytes(StandardCharsets.UTF_8));
        this.sentMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();

        this.step = step;
        this.clientRound1 = clientRound1;
        this.serverRound1 = serverRound1;
        this.clientRound2 = clientRound2;
        this.serverRound2 = serverRound2;
    }

    public JpakeAuthBuilder(String pairingCode) {
        this(pairingCode, JpakeStep.INITIAL, null, null, null, null);
    }

    private static JpakeAuthBuilder INSTANCE = null;
    public static JpakeAuthBuilder getInstance(String pairingCode) {
        if (INSTANCE == null || !INSTANCE.pairingCode.equals(pairingCode)) {
            INSTANCE = new JpakeAuthBuilder(pairingCode);
        }
        return INSTANCE;
    }

    public static JpakeAuthBuilder getInstance() {
        if (INSTANCE == null) {
            throw new IllegalArgumentException("JPAKE auth session does not exist");
        }
        return INSTANCE;
    }

    public Message nextRequest() {
        Message request;
        if (step == JpakeStep.INITIAL) {
            this.clientRound1 = this.cli.getRound1();
            byte[] challenge = Arrays.copyOfRange(this.clientRound1, 0, 165);

            L.i(TAG, "Req1a: " + Hex.encodeHexString(challenge));
            request = new Jpake1aRequest(0, challenge);

            step = JpakeStep.ROUND_1A_SENT;
        } else if (step == JpakeStep.ROUND_1A_RECEIVED) {
            byte[] challenge = Arrays.copyOfRange(this.clientRound1, 165, 330);

            L.i(TAG, "Req1b: " + Hex.encodeHexString(challenge));
            request = new Jpake1bRequest(0, challenge);

            step = JpakeStep.ROUND_1B_SENT;
        } else if (step == JpakeStep.ROUND_1B_RECEIVED) {
            this.clientRound2 = this.cli.getRound2();
            byte[] challenge = Arrays.copyOfRange(this.clientRound2, 0, 165);
            L.i(TAG, "Req2: " + Hex.encodeHexString(challenge));
            request = new Jpake2Request(0, challenge);

            step = JpakeStep.ROUND_2_SENT;
        } else if (step == JpakeStep.ROUND_2_RECEIVED) {
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
            L.i(TAG, "Res1a: " + Hex.encodeHexString(m.getCentralChallengeHash()));
            this.serverRound1 = m.getCentralChallengeHash();

            step = JpakeStep.ROUND_1A_RECEIVED;
        } else if (response instanceof Jpake1bResponse) {
            Jpake1bResponse m = (Jpake1bResponse) response;
            L.i(TAG, "Res1b: " + Hex.encodeHexString(m.getCentralChallengeHash()));
            byte[] fullServerRound1 = Bytes.combine(this.serverRound1, m.getCentralChallengeHash());
            L.i(TAG, "FULL_ROUND_1_RESPONSE: " + Hex.encodeHexString(fullServerRound1));
            this.serverRound1 = fullServerRound1;

            this.cli.readRound1(fullServerRound1);
            step = JpakeStep.ROUND_1B_RECEIVED;
        } else if (response instanceof Jpake2Response) {
            Jpake2Response m = (Jpake2Response) response;
            L.i(TAG, "Res2: " + Hex.encodeHexString(m.getCentralChallengeHash()));
            this.serverRound2 = m.getCentralChallengeHash();
            L.i(TAG, "FULL_ROUND_2_RESPONSE: " + Hex.encodeHexString(this.serverRound2));

            this.cli.readRound2(this.serverRound2);
            step = JpakeStep.ROUND_2_RECEIVED;
        }
    }

    public enum JpakeStep {
        INITIAL,
        ROUND_1A_SENT,
        ROUND_1A_RECEIVED,
        ROUND_1B_SENT,
        ROUND_1B_RECEIVED,
        ROUND_2_SENT,
        ROUND_2_RECEIVED,

    }
}
