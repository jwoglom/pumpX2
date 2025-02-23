package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentExitFillTubingModeStateStreamRequest;

import java.math.BigInteger;

/**
 * Stream message sent by pump while exiting fill tubing mode.
 *
 * Transaction ID of this stream message matches the call to ExitFillTubingModeRequest
 */
@MessageProps(
    opCode=-23,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request=NonexistentExitFillTubingModeStateStreamRequest.class,
    stream=true,
    signed=true
)
public class ExitFillTubingModeStateStreamResponse extends Message {
    
    private int state;
    
    public ExitFillTubingModeStateStreamResponse() {}
    
    public ExitFillTubingModeStateStreamResponse(int state) {
        this.cargo = buildCargo(state);
        this.state = state;
        
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.state = raw[0];
        
    }

    
    public static byte[] buildCargo(int state) {
        return Bytes.combine(
            new byte[]{ (byte) state });
    }
    
    public int getState() {
        return state;
    }
    
}