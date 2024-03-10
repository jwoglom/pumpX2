package com.jwoglom.pumpx2.pump.messages.response.authentication;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.authentication.FourthChallengeV2Request;

import java.util.Arrays;

@MessageProps(
    opCode=39,
    size=18,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.AUTHORIZATION,
    request=FourthChallengeV2Request.class
)
public class FourthChallengeV2Response extends Message {
    private int appInstanceId;
    private long unknownField1;
    private long unknownField2;
    private long unknownField3;
    private long unknownField4;

    public FourthChallengeV2Response() {}

    public FourthChallengeV2Response(int appInstanceId, long unknownField1, long unknownField2, long unknownField3, long unknownField4) {
        parse(buildCargo(appInstanceId, unknownField1, unknownField2, unknownField3, unknownField4));
        Preconditions.checkState(this.appInstanceId == appInstanceId);
        Preconditions.checkState(this.unknownField1 == unknownField1);
        Preconditions.checkState(this.unknownField2 == unknownField2);
    }

    public FourthChallengeV2Response(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.appInstanceId = Bytes.readShort(raw, 0);
        this.unknownField1 = Bytes.readUint32(raw, 2);
        this.unknownField2 = Bytes.readUint32(raw, 6);
        this.unknownField3 = Bytes.readUint32(raw, 10);
        this.unknownField4 = Bytes.readUint32(raw, 14);
    }

    public static byte[] buildCargo(int appInstanceId, long unknownField1, long unknownField2, long unknownField3, long unknownField4) {
        return Bytes.combine(
                Bytes.firstTwoBytesLittleEndian(appInstanceId),
                Bytes.toUint64(unknownField1),
                Bytes.toUint64(unknownField2),
                Bytes.toUint64(unknownField3),
                Bytes.toUint64(unknownField4)
        );
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public long getUnknownField1() {
        return unknownField1;
    }

    public long getUnknownField2() {
        return unknownField2;
    }

    public long getUnknownField3() {
        return unknownField3;
    }

    public long getUnknownField4() {
        return unknownField4;
    }
}
