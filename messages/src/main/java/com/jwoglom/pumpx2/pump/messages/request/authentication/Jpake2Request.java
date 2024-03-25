package com.jwoglom.pumpx2.pump.messages.request.authentication;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.authentication.Jpake2Response;

import java.util.Arrays;

/**
 * The third authorization message sent to the pump with V2 authorization style which contains the
 * second round of EcJpake. The message structure is identical to {@link Jpake1aRequest} and
 * {@link Jpake1bRequest}, the second EcJpake round has half the length though so does not need to
 * be split into two sub-messages.
 *
 * When the Android libary is used, this message is invoked automatically by PumpX2 on Bluetooth
 * connection initialization as part of performing the initial pump pairing process.
 */
@MessageProps(
    opCode=36,
    size=167,
    type=MessageType.REQUEST,
    minApi=KnownApiVersion.API_V3_2,
    characteristic=Characteristic.AUTHORIZATION,
    response= Jpake2Response.class
)
public class Jpake2Request extends Message {
    private int appInstanceId;
    private byte[] centralChallenge;

    public Jpake2Request() {}

    public Jpake2Request(int appInstanceId, byte[] centralChallenge) {
        this.cargo = buildCargo(appInstanceId, centralChallenge);
        this.appInstanceId = appInstanceId;
        this.centralChallenge = centralChallenge; // 165
    }

    public Jpake2Request(byte[] rawCargo) {
        parse(rawCargo);
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getCentralChallenge() {
        return centralChallenge;
    }

    private static byte[] buildCargo(int appInstanceId, byte[] centralChallenge) {
        byte[] cargo = new byte[167];
        System.arraycopy(Bytes.combine(Bytes.firstTwoBytesLittleEndian(appInstanceId), centralChallenge), 0, cargo, 0, 167);

        return cargo;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
        this.appInstanceId = Bytes.readShort(Arrays.copyOfRange(raw, 0, 2), 0);
        this.centralChallenge = Arrays.copyOfRange(raw, 2, raw.length);
    }
}
