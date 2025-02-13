package com.jwoglom.pumpx2.pump.messages.response.controlStream;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.controlStream.NonexistentFillTubingStateStreamRequest;

/*
 * Response message received via stream while tubing is being filled.
 *
 * The buttonState is set to 1 when the pump button is pressed down,
 * and 0 when released. Since it's a stream message, there is no initiating request.
 *
 * NOTE: this message still has a txid which matches the txid of the call to control.EnterFillTubingModeRequest
 *
 *
 */
@MessageProps(
    opCode=-27,
    size=1, // 25 with 24 byte padding
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL_STREAM,
    request=NonexistentFillTubingStateStreamRequest.class,
    stream=true,
    signed=true
)
public class FillTubingStateStreamResponse extends Message {
    
    private int buttonState;
    
    public FillTubingStateStreamResponse() {}
    
    public FillTubingStateStreamResponse(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.buttonState = raw[0];
        
    }

    
    public static byte[] buildCargo(int status) {
        return Bytes.combine(
            new byte[]{(byte) status}
        );
    }
    
    public int getButtonState() {
        return buttonState;
    }

    public boolean getButtonDown() {
        return buttonState == 1;
    }
    
}