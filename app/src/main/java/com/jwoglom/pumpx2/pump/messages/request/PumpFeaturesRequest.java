package com.jwoglom.pumpx2.pump.messages.request;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.PumpFeaturesResponse;

@MessageProps(
    opCode=78,
    size=0,
    type=MessageType.REQUEST,
    response=PumpFeaturesResponse.class
)
public class PumpFeaturesRequest extends Message {
    

    public PumpFeaturesRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
    }
}