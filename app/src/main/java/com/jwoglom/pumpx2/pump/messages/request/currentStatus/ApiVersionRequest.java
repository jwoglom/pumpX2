package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse;

@MessageProps(
    opCode=32,
    size=0,
    type=MessageType.REQUEST,
    response=ApiVersionResponse.class
)
public class ApiVersionRequest extends Message {
    public ApiVersionRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
