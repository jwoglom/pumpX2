package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake3SessionKeyRequest;
import com.jwoglom.pumpx2.shared.Hex;

import java.util.Arrays;

@MessageProps(
    opCode=39,
    size=18,
    type=MessageType.RESPONSE,
    minApi=KnownApiVersion.API_V3_2,
    characteristic=Characteristic.AUTHORIZATION,
    request=Jpake3SessionKeyRequest.class
)
public class Jpake3SessionKeyResponse extends Message {
    private int appInstanceId;
    private byte[] deviceKeyNonce;
    private byte[] deviceKeyReserved;

    public Jpake3SessionKeyResponse() {}

    public Jpake3SessionKeyResponse(byte[] raw) {
        parse(raw);
    }

    public Jpake3SessionKeyResponse(int appInstanceId, byte[] nonce, byte[] reserved) {
        parse(buildCargo(appInstanceId, nonce, reserved));
        Preconditions.checkState(this.appInstanceId == appInstanceId);
        Preconditions.checkState(Hex.encodeHexString(this.deviceKeyNonce).equals(Hex.encodeHexString(nonce)));
        Preconditions.checkState(Hex.encodeHexString(this.deviceKeyReserved).equals(Hex.encodeHexString(reserved)));
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.appInstanceId = Bytes.readShort(Arrays.copyOfRange(raw, 0, 2), 0);
        this.deviceKeyNonce = Arrays.copyOfRange(raw, 2, 10); // 8
        this.deviceKeyReserved = Arrays.copyOfRange(raw, 10, 18); // 8
    }

    public static byte[] buildCargo(int appInstanceId, byte[] nonce, byte[] reserved) {
        return Bytes.combine(
                Bytes.firstTwoBytesLittleEndian(appInstanceId),
                nonce,
                reserved
        );
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getDeviceKeyNonce() {
        return deviceKeyNonce;
    }

    public byte[] getDeviceKeyReserved() {
        return deviceKeyReserved;
    }
}
