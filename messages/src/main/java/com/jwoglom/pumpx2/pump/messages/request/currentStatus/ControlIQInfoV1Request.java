package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQInfoV1Response;

@MessageProps(
    opCode=104,
    size=0,
    type=MessageType.REQUEST,
    response= ControlIQInfoV1Response.class
)
public class ControlIQInfoV1Request extends Message {
    public ControlIQInfoV1Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
    }

    
}