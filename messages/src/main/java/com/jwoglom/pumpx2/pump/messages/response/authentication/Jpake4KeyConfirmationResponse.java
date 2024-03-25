package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake4KeyConfirmationRequest;
import com.jwoglom.pumpx2.shared.Hex;

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
    private byte[] centralChallenge;

    public Jpake4KeyConfirmationResponse() {}

    public Jpake4KeyConfirmationResponse(int appInstanceId, byte[] centralChallenge) {
        parse(buildCargo(appInstanceId, centralChallenge));
        Preconditions.checkState(this.appInstanceId == appInstanceId);
        Preconditions.checkState(Hex.encodeHexString(this.centralChallenge).equals(Hex.encodeHexString(centralChallenge)));
    }

    public Jpake4KeyConfirmationResponse(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.appInstanceId = Bytes.readShort(raw, 0);
        this.centralChallenge = Bytes.dropFirstN(raw, 2);
    }

    public static byte[] buildCargo(int appInstanceId, byte[] centralChallenge) {
        return Bytes.combine(
                Bytes.firstTwoBytesLittleEndian(appInstanceId),
                centralChallenge
        );
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getCentralChallenge() {
        return centralChallenge;
    }
}
