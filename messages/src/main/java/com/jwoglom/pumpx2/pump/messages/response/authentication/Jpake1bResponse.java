package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake1bRequest;

import java.util.Arrays;

@MessageProps(
    opCode=35,
    size=167,
    type=MessageType.RESPONSE,
    minApi=KnownApiVersion.API_V3_2,
    characteristic=Characteristic.AUTHORIZATION,
    request= Jpake1bRequest.class
)
public class Jpake1bResponse extends Message {
    private int appInstanceId;
    private byte[] centralChallengeHash;

    public Jpake1bResponse() {}

    public Jpake1bResponse(int appInstanceId, byte[] centralChallengeHash) {
        parse(buildCargo(appInstanceId, centralChallengeHash));
        Preconditions.checkState(this.appInstanceId == appInstanceId);
        Preconditions.checkState(Arrays.equals(this.centralChallengeHash, centralChallengeHash));
    }

    public Jpake1bResponse(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        appInstanceId = Bytes.readShort(raw, 0);
        centralChallengeHash = Arrays.copyOfRange(raw, 2, 167); // 165 == Request.centralChallenge.length
    }

    public static byte[] buildCargo(int byte0short, byte[] bytes2to167) {
        return Bytes.combine(Bytes.firstTwoBytesLittleEndian(byte0short), bytes2to167);
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getCentralChallengeHash() {
        return centralChallengeHash;
    }
}