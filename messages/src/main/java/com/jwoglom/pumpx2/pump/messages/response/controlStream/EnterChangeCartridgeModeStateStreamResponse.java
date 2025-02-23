package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentEnterChangeCartridgeModeStateStreamRequest;

/**
 * Transaction ID of this stream message matches the call to EnterChangeCartridgeModeRequest
 */
@MessageProps(
    opCode=-31,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request=NonexistentEnterChangeCartridgeModeStateStreamRequest.class,
    stream=true,
    signed=true
)
public class EnterChangeCartridgeModeStateStreamResponse extends Message {
    
    private int state;
    
    public EnterChangeCartridgeModeStateStreamResponse() {}
    
    public EnterChangeCartridgeModeStateStreamResponse(int state) {
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