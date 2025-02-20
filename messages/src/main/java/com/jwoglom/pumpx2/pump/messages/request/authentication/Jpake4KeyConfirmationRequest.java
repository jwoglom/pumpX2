package com.jwoglom.pumpx2.pump.messages.request.authentication;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake4KeyConfirmationResponse;

import java.util.Arrays;

/**
 * The fifth authorization message sent to the pump with V2 authorization style.
 *
 * When the Android libary is used, this message is invoked automatically by PumpX2 on Bluetooth
 * connection initialization as part of performing the initial pump pairing process.
 */
@MessageProps(
    opCode=40,
    size=50,
    type=MessageType.REQUEST,
    minApi=KnownApiVersion.API_V3_2,
    characteristic=Characteristic.AUTHORIZATION,
    response=Jpake4KeyConfirmationResponse.class
)
public class Jpake4KeyConfirmationRequest extends Message {
    private int appInstanceId;
    private byte[] hashDigest;
    private byte[] reserved;
    private byte[] nonce;

    public static byte[] RESERVED = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    public Jpake4KeyConfirmationRequest() {}


    public Jpake4KeyConfirmationRequest(int appInstanceId, byte[] nonce, byte[] reserved, byte[] hashDigest) {
        this.cargo = buildCargo(appInstanceId, nonce, reserved, hashDigest);
        this.appInstanceId = appInstanceId;
        this.nonce = nonce;
        this.reserved = reserved;
        this.hashDigest = hashDigest;
    }

    public Jpake4KeyConfirmationRequest(byte[] rawCargo) {
        parse(rawCargo);
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getNonce() {
        return nonce;
    }

    public byte[] getHashDigest() {
        return hashDigest;
    }

    public byte[] getReserved() {
        return reserved;
    }

    private static byte[] buildCargo(int appInstanceId, byte[] nonce, byte[] reserved, byte[] hashDigest) {
        Validate.isTrue(nonce.length == 8, "nonce was " + nonce.length + " not 8");
        Validate.isTrue(reserved.length == 8);
        Validate.isTrue(hashDigest.length == 32, "hashDigest was " + hashDigest.length + " not 32");
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
