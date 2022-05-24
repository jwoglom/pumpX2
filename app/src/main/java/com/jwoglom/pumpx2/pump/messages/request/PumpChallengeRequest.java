package com.jwoglom.pumpx2.pump.messages.request;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.PumpChallengeResponse;

import java.util.Arrays;

import kotlin.collections.ArraysKt;

@MessageProps(
    opCode=18,
    size=22,
    type=MessageType.REQUEST,
    response=PumpChallengeResponse.class
)
public class PumpChallengeRequest extends Message {
    private int appInstanceId;
    private byte[] pumpChallengeHash;

    public PumpChallengeRequest() {}

    public PumpChallengeRequest(int appInstanceId, byte[] pumpChallengeHash) {
        parse(buildCargo(appInstanceId, pumpChallengeHash));
        Preconditions.checkState(this.appInstanceId == appInstanceId);
        Preconditions.checkState(Arrays.equals(this.pumpChallengeHash, pumpChallengeHash));
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getPumpChallengeHash() {
        return pumpChallengeHash;
    }

    private static byte[] buildCargo(int appInstanceId, byte[] pumpChallengeHash) {
        byte[] cargo = new byte[22];
        System.arraycopy(ArraysKt.plus(Bytes.firstTwoBytesLittleEndian(appInstanceId), pumpChallengeHash), 0, cargo, 0, 22);

        return cargo;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
        this.appInstanceId = Bytes.readShort(Arrays.copyOfRange(raw, 0, 2), 0);
        this.pumpChallengeHash = Arrays.copyOfRange(raw, 2, raw.length);
    }
}
