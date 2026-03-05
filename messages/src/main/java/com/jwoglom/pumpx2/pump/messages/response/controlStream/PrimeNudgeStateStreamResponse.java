package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
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
public class PrimeNudgeStateStreamResponse extends ExitFillTubingModeStateStreamResponse {

    public PrimeNudgeStateStreamResponse() {}

    public PrimeNudgeStateStreamResponse(int stateId) {
        super(stateId);
    }

    public static byte[] buildCargo(int state) {
        return ExitFillTubingModeStateStreamResponse.buildCargo(state);
    }

    public int getStateId() {
        return super.getStateId();
    }
}
