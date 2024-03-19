package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.authentication.CentralChallengeV2Request;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeV2Request;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeV2Response;
import com.jwoglom.pumpx2.shared.Hex;
import com.jwoglom.pumpx2.shared.L;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.particle.crypto.EcJpake;

public class JpakeAuthBuilder {
    private static String TAG = "JPAKE";

    private String pairingCode;
    private List<Message> sentMessages;
    private List<Message> receivedMessages;
    private EcJpake cli;

    private int round = 1;

    public JpakeAuthBuilder(String pairingCode, List<Message> sentMessages, List<Message> receivedMessages) {
        this.pairingCode = pairingCode;
        this.cli = new EcJpake(EcJpake.Role.CLIENT, pairingCode.getBytes(StandardCharsets.UTF_8));
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

    public Message buildNextRequest() {
        if (round == 1) {
            byte[] challenge = this.cli.getRound1();
            L.i(TAG, "Req1: " + Hex.encodeHexString(challenge));
            return new CentralChallengeV2Request(0, challenge);
        } else if (round == 2) {
            byte[] challenge = this.cli.getRound2();
            L.i(TAG, "Req2: " + Hex.encodeHexString(challenge));
            return new PumpChallengeV2Request(0, challenge);
        }
        return null;
    }

    public void processResponse(Message response) {
        if (response instanceof CentralChallengeV2Response) {
            CentralChallengeV2Response m = (CentralChallengeV2Response)response;
            L.i(TAG, "Res1: " + Hex.encodeHexString(m.getCentralChallengeHash()));
            this.cli.readRound1(m.getCentralChallengeHash());
        }
    }
}
