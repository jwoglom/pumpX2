package com.jwoglom.pumpx2.pump.messages.request.authentication;

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
    private byte[] centralChallenge;

    public Jpake4KeyConfirmationRequest() {}

    public Jpake4KeyConfirmationRequest(int appInstanceId, byte[] centralChallenge) {
        this.cargo = buildCargo(appInstanceId, centralChallenge);
        this.appInstanceId = appInstanceId;
        this.centralChallenge = centralChallenge;
    }

    public Jpake4KeyConfirmationRequest(byte[] rawCargo) {
        parse(rawCargo);
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getCentralChallenge() {
        return centralChallenge;
    }

    private static byte[] buildCargo(int appInstanceId, byte[] centralChallenge) {
        byte[] cargo = new byte[50];
        System.arraycopy(Bytes.combine(Bytes.firstTwoBytesLittleEndian(appInstanceId), centralChallenge), 0, cargo, 0, 50);

        return cargo;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
        this.appInstanceId = Bytes.readShort(Arrays.copyOfRange(raw, 0, 2), 0);
        this.centralChallenge = Bytes.dropFirstN(raw, 2);
    }
}
