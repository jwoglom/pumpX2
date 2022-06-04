package com.jwoglom.pumpx2.pump.messages.request;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.ErrorResponse;

@MessageProps(
        opCode=-99999999,
        size=0,
        type=MessageType.REQUEST,
        response= ErrorResponse.class
)
public class UndefinedRequest extends Message {

    public UndefinedRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }

}