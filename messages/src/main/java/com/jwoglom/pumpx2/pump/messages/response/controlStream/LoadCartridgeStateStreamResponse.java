package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentLoadCartridgeStateStreamRequest;

@MessageProps(
    opCode=-29,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request=NonexistentLoadCartridgeStateStreamRequest.class,
    stream=true,
    signed=true
)
public class LoadCartridgeStateStreamResponse extends DetectingCartridgeStateStreamResponse {

    public LoadCartridgeStateStreamResponse() {}

    public LoadCartridgeStateStreamResponse(int stateId) {
        super(buildCargo(stateId));
    }

    public static byte[] buildCargo(int state) {
        return DetectingCartridgeStateStreamResponse.buildLoadCartridgeCargo(state);
    }

    public int getStateId() {
        return super.getStateId();
    }
}
