package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeV2Request;
import com.jwoglom.pumpx2.pump.messages.request.authentication.ThirdChallengeV2Request;

import java.util.Arrays;

@MessageProps(
    opCode=37,
    size=170, // 3 bytes longer than second
    type=MessageType.RESPONSE,
    characteristic=Characteristic.AUTHORIZATION,
    request=ThirdChallengeV2Request.class
)
public class ThirdChallengeV2Response extends Message {
    private int appInstanceId;
    private byte[] centralChallengeHash;

    public ThirdChallengeV2Response() {}

    public ThirdChallengeV2Response(int appInstanceId, byte[] centralChallengeHash) {
        parse(buildCargo(appInstanceId, centralChallengeHash));
        Preconditions.checkState(this.appInstanceId == appInstanceId);
        Preconditions.checkState(Arrays.equals(this.centralChallengeHash, centralChallengeHash));
    }

    public ThirdChallengeV2Response(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        appInstanceId = Bytes.readShort(raw, 0);
        centralChallengeHash = Arrays.copyOfRange(raw, 2, 170); // 168 == 3 greater than normal
    }

    public static byte[] buildCargo(int byte0short, byte[] bytes2to170) {
        return Bytes.combine(Bytes.firstTwoBytesLittleEndian(byte0short), bytes2to170);
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getCentralChallengeHash() {
        return centralChallengeHash;
    }
}
