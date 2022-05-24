package com.jwoglom.pumpx2.pump.messages.request;

import static com.jwoglom.pumpx2.pump.messages.Message.EMPTY;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.AlertStatusResponse;

@MessageProps(
        opCode=68,
        size=0,
        type= MessageType.REQUEST,
        response= AlertStatusResponse.class
)
public class AlertStatusRequest extends Message {
    public AlertStatusRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
