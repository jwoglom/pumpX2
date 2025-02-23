package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentDetectingCartridgeStateStreamRequest;

/**
 * Stream message sent by pump while the newly inserted cartridge is being checked.
 *
 * Transaction ID of this stream message matches the call to ExitChangeCartridgeModeRequest
 */
@MessageProps(
    opCode=-29,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request= NonexistentDetectingCartridgeStateStreamRequest.class,
    stream=true,
    signed=true
)
public class DetectingCartridgeStateStreamResponse extends Message {
    
    private int percentComplete;
    
    public DetectingCartridgeStateStreamResponse() {}
    
    public DetectingCartridgeStateStreamResponse(int percentComplete) {
        this.cargo = buildCargo(percentComplete);
        this.percentComplete = percentComplete;
        
    }
    public DetectingCartridgeStateStreamResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);

    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.percentComplete = Bytes.readShort(raw, 0);
        
    }

    
    public static byte[] buildCargo(int state) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(state));
    }
    
    public int getPercentComplete() {
        return percentComplete;
    }

    public boolean isComplete() {
        return percentComplete == 100;
    }

}