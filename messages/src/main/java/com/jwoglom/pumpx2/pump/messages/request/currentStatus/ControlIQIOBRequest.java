package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQIOBResponse;

@MessageProps(
        opCode=108,
        size=0,
        type=MessageType.REQUEST,
        response= ControlIQIOBResponse.class
)
public class ControlIQIOBRequest extends Message {
    public ControlIQIOBRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
