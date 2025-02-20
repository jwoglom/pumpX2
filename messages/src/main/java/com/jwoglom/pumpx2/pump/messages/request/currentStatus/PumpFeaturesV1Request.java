package com.jwoglom.pumpx2.pump.messages.request.currentStatus;


import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesV1Response;

import org.apache.commons.lang3.Validate;

@MessageProps(
    opCode=78,
    size=0,
    type=MessageType.REQUEST,
    response=PumpFeaturesV1Response.class
)
public class PumpFeaturesV1Request extends Message {
    

    public PumpFeaturesV1Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
    }
}