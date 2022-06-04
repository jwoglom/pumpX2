package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.authentication.CentralChallengeRequest;

import java.util.Arrays;

import kotlin.collections.ArraysKt;

@MessageProps(
    opCode=17,
    size=30,
    type=MessageType.RESPONSE,
    request=CentralChallengeRequest.class
)
public class CentralChallengeResponse extends Message {
    private int byte0short;
    private byte[] bytes2to22;
    private byte[] bytes22to30;

    public CentralChallengeResponse() {}

    public CentralChallengeResponse(int byte0short, byte[] bytes2to22, byte[] bytes22to30) {
        parse(buildCargo(byte0short, bytes2to22, bytes22to30));
        Preconditions.checkState(this.byte0short == byte0short);
        Preconditions.checkState(Arrays.equals(this.bytes2to22, bytes2to22));
        Preconditions.checkState(Arrays.equals(this.bytes22to30, bytes22to30));
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        byte0short = Bytes.readShort(raw, 0);
        bytes2to22 = Arrays.copyOfRange(raw, 2, 22);
        bytes22to30 = Arrays.copyOfRange(raw, 22, 30);
    }

    public static byte[] buildCargo(int byte0short, byte[] bytes2to22, byte[] bytes22to30) {
        return ArraysKt.plus(Bytes.firstTwoBytesLittleEndian(byte0short), ArraysKt.plus(bytes2to22, bytes22to30));
    }

    public int getByte0short() {
        return byte0short;
    }

    public byte[] getBytes2to22() {
        return bytes2to22;
    }

    public byte[] getBytes22to30() {
        return bytes22to30;
    }
}
