package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeV2Request;
import com.jwoglom.pumpx2.pump.messages.request.authentication.ThirdChallengeV2Request;

import java.util.Arrays;

@MessageProps(
    opCode=37,
    size=170, // 3 bytes longer than second
    type=MessageType.RESPONSE,
    minApi=KnownApiVersion.API_V3_2,
    characteristic=Characteristic.AUTHORIZATION,
    request=ThirdChallengeV2Request.class
)
public class ThirdChallengeV2Response extends Message {
    private int appInstanceId;
    private byte[] unknown3b;
    private byte[] centralChallengeHash;

    public ThirdChallengeV2Response() {}

    public ThirdChallengeV2Response(int appInstanceId, byte[] unknown3b, byte[] centralChallengeHash) {
        parse(buildCargo(appInstanceId, unknown3b, centralChallengeHash));
        Preconditions.checkState(this.appInstanceId == appInstanceId);
        Preconditions.checkState(Arrays.equals(this.unknown3b, unknown3b));
        Preconditions.checkState(Arrays.equals(this.centralChallengeHash, centralChallengeHash));
    }

    public ThirdChallengeV2Response(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        appInstanceId = Bytes.readShort(raw, 0);
        unknown3b = Arrays.copyOfRange(raw, 2, 5);
        centralChallengeHash = Arrays.copyOfRange(raw, 5, 170); // 165
    }

    public static byte[] buildCargo(int byte0short, byte[] bytes2to5, byte[] bytes5to170) {
        return Bytes.combine(Bytes.firstTwoBytesLittleEndian(byte0short), bytes2to5, bytes5to170);
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public byte[] getUnknown3b() {
        return unknown3b;
    }

    public byte[] getCentralChallengeHash() {
        return centralChallengeHash;
    }
}
