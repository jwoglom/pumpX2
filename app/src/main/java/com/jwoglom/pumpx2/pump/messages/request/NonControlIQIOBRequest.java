package com.jwoglom.pumpx2.pump.messages.request;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.NonControlIQIOBResponse;

/**
 * ControlIQIOBRequest should be used instead if control IQ is supported
 */
@MessageProps(
        opCode=38,
        size=0,
        type=MessageType.REQUEST,
        response= NonControlIQIOBResponse.class
)
public class NonControlIQIOBRequest extends Message {
    public NonControlIQIOBRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
