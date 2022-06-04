package com.jwoglom.pumpx2.pump.messages.request.authentication;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;

import java.util.Arrays;

import kotlin.collections.ArraysKt;

@MessageProps(
    opCode=16,
    size=10,
    type=MessageType.REQUEST,
    response=CentralChallengeResponse.class
)
public class CentralChallengeRequest extends Message {
    private int appInstanceId;
    private byte[] centralChallenge;

    public CentralChallengeRequest() {}

    public CentralChallengeRequest(int appInstanceId, byte[] centralChallenge) {
        this.cargo = buildCargo(appInstanceId, centralChallenge);
        this.appInstanceId = appInstanceId;
        this.centralChallenge = centralChallenge;
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getCentralChallenge() {
        return centralChallenge;
    }

    private static byte[] buildCargo(int appInstanceId, byte[] centralChallenge) {
        byte[] cargo = new byte[10];
        System.arraycopy(ArraysKt.plus(Bytes.firstTwoBytesLittleEndian(appInstanceId), centralChallenge), 0, cargo, 0, 10);

        return cargo;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
        this.appInstanceId = Bytes.readShort(Arrays.copyOfRange(raw, 0, 2), 0);
        this.centralChallenge = Arrays.copyOfRange(raw, 2, raw.length);
    }
}
