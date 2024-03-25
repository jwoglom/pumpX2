package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake4KeyConfirmationRequest;

import java.util.Arrays;

@MessageProps(
    opCode=41,
    size=50,
    type=MessageType.RESPONSE,
    minApi=KnownApiVersion.API_V3_2,
    characteristic=Characteristic.AUTHORIZATION,
    request= Jpake4KeyConfirmationRequest.class
)
public class Jpake4KeyConfirmationResponse extends Message {
    private int appInstanceId;
    private byte[] nonce;
    private byte[] reserved;
    private byte[] hashDigest;

    public static byte[] RESERVED = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    public Jpake4KeyConfirmationResponse() {}


    public Jpake4KeyConfirmationResponse(int appInstanceId, byte[] nonce, byte[] reserved, byte[] hashDigest) {
        this.cargo = buildCargo(appInstanceId, nonce, reserved, hashDigest);
        this.appInstanceId = appInstanceId;
        this.nonce = nonce;
        this.reserved = reserved;
        this.hashDigest = hashDigest;
    }

    public Jpake4KeyConfirmationResponse(byte[] rawCargo) {
        parse(rawCargo);
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public byte[] getReserved() {
        return reserved;
    }

    public byte[] getHashDigest() {
        return hashDigest;
    }

    private static byte[] buildCargo(int appInstanceId, byte[] nonce, byte[] reserved, byte[] hashDigest) {
        Preconditions.checkArgument(nonce.length == 8);
        Preconditions.checkArgument(reserved.length == 8);
        Preconditions.checkArgument(hashDigest.length == 32);
        byte[] cargo = new byte[50];
        System.arraycopy(Bytes.combine(
                Bytes.firstTwoBytesLittleEndian(appInstanceId),
                nonce,
                reserved,
                hashDigest
        ), 0, cargo, 0, 50);

        return cargo;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
        this.appInstanceId = Bytes.readShort(Arrays.copyOfRange(raw, 0, 2), 0);
        this.nonce = Arrays.copyOfRange(raw, 2, 10);
        this.reserved = Arrays.copyOfRange(raw, 10, 18);
        this.hashDigest = Arrays.copyOfRange(raw, 18, 50); // 32
    }
}
