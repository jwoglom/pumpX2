package com.jwoglom.pumpx2.pump.messages.response.authentication;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeRequest;

import kotlin.collections.ArraysKt;

@MessageProps(
    opCode=19,
    size=3,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.AUTHORIZATION,
    request=PumpChallengeRequest.class
)
// aka AuthenticationStatusResponse
public class PumpChallengeResponse extends AbstractPumpChallengeResponse {
    private int appInstanceId;
    private boolean success;

    public PumpChallengeResponse() {}

    public PumpChallengeResponse(int appInstanceId, boolean success) {
        parse(buildCargo(appInstanceId, success));
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        appInstanceId = Bytes.readShort(raw, 0);
        success = raw[2] == 1;
    }

    public static byte[] buildCargo(int appInstanceId, boolean success) {
        return Bytes.combine(Bytes.firstTwoBytesLittleEndian(appInstanceId), new byte[]{(byte)(success ? 1 : 0)});
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
