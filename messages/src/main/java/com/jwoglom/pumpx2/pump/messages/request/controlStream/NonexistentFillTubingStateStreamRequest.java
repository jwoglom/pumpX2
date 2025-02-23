package com.jwoglom.pumpx2.pump.messages.request.controlStream;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.response.controlStream.FillTubingStateStreamResponse;
import com.jwoglom.pumpx2.pump.messages.response.controlStream.PumpingStateStreamResponse;

/**
 * This message is used as a paired request message for ControlStreamNeg27Response,
 * which does not have an originating request.
 */
@MessageProps(
        opCode=0, // nonexistent
        size=0,
        type=MessageType.REQUEST,
        characteristic=Characteristic.CONTROL_STREAM,
        response=FillTubingStateStreamResponse.class,
        stream=true,
        signed=true
)
public class NonexistentFillTubingStateStreamRequest extends Message {

    public NonexistentFillTubingStateStreamRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {

    }

}