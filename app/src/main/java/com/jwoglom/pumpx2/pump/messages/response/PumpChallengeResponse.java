package com.jwoglom.pumpx2.pump.messages.response;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.PumpChallengeRequest;

import kotlin.collections.ArraysKt;

@MessageProps(
    opCode=19,
    size=3,
    type=MessageType.RESPONSE,
    request=PumpChallengeRequest.class
)
// aka AuthenticationStatusResponse
public class PumpChallengeResponse extends Message {
    private int appInstanceId;
    private boolean success;

    public PumpChallengeResponse() {}

    public PumpChallengeResponse(int appInstanceId, boolean success) {
        parse(buildCargo(appInstanceId, success));
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        appInstanceId = Bytes.readShort(raw, 0);
        success = raw[2] == 1;
    }

    public static byte[] buildCargo(int appInstanceId, boolean success) {
        return ArraysKt.plus(Bytes.firstTwoBytesLittleEndian(appInstanceId), new byte[]{(byte)(success ? 1 : 0)});
    }

    public int getAppInstanceId() {
        return appInstanceId;
    }

    public boolean getSuccess() {
        return success;
    }
    public boolean hasSuccess() {
        return success;
    }

}
