package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentPumpingStateStreamRequest;

@MessageProps(
    opCode=-23,
    size=5, // 29 with 24 byte padding
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request=NonexistentPumpingStateStreamRequest.class,
    stream=true,
    signed=true
)
public class PumpingStateStreamResponse extends ExitFillTubingModeStateStreamResponse {
    
    public PumpingStateStreamResponse() {}
    
    public PumpingStateStreamResponse(boolean isPumpingStateSetAfterStartUp, long stateBitmask) {
        super(isPumpingStateSetAfterStartUp, stateBitmask);
    }

    
    public static byte[] buildCargo(boolean isPumpingStateSetAfterStartUp, long stateBitmask) {
        return ExitFillTubingModeStateStreamResponse.buildPumpingCargo(isPumpingStateSetAfterStartUp, stateBitmask);
    }
    
    public boolean getIsPumpingStateSetAfterStartUp() {
        return super.getIsPumpingStateSetAfterStartUp();
    }
    public long getStateBitmask() {
        return super.getStateBitmask();
    }
}
