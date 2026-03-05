package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentPrimeNudgeStateStreamRequest;

@MessageProps(
    opCode=-23,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request=NonexistentPrimeNudgeStateStreamRequest.class,
    stream=true,
    signed=true
)
public class PrimeNudgeStateStreamResponse extends Message {

    private int stateId;

    public PrimeNudgeStateStreamResponse() {}

    public PrimeNudgeStateStreamResponse(int stateId) {
        this.cargo = buildCargo(stateId);
        parse(cargo);
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.stateId = raw[0];
    }

    public static byte[] buildCargo(int state) {
        return Bytes.combine(
            new byte[]{ (byte) state });
    }

    public int getStateId() {
        return stateId;
    }
}
