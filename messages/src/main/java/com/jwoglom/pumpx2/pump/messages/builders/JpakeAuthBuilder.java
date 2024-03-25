package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake1aRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake1bRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake1aResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake1bResponse;
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
    private EcJpake cli;

    private JpakeStep step = JpakeStep.INITIAL;

    public JpakeAuthBuilder(String pairingCode, List<Message> sentMessages, List<Message> receivedMessages) {
        this.pairingCode = pairingCode;
        this.cli = new EcJpake(EcJpake.Role.CLIENT, pairingCode.getBytes(StandardCharsets.UTF_8));
        this.sentMessages = sentMessages;
        this.receivedMessages = receivedMessages;
        this.clientRound1 = null;
        this.serverRound1 = null;
//        for (int i=0; i<sentMessages.size(); i++) {
//            processRequest(sentMessages.get(i));
//            if (i < receivedMessages.size()) {
//                processResponse(receivedMessages.get(i));
//            }
//        }
    }

    public JpakeAuthBuilder(String pairingCode) {
        this(pairingCode, new ArrayList<Message>(), new ArrayList<Message>());
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
            //byte[] challenge = this.cli.getRound2();
            L.i(TAG, "Req2: " + Hex.encodeHexString(challenge));
            request = new Jpake1bRequest(0, challenge);
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
        }
    }

    public enum JpakeStep {
        INITIAL,
        ROUND_1A_SENT,
        ROUND_1A_RECEIVED,
        ROUND_1B_SENT,
        ROUND_1B_RECEIVED,

    }
}
