package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBatteryV1Response;

@MessageProps(
    opCode=52,
    size=0,
    type=MessageType.REQUEST,
    response=CurrentBatteryV1Response.class
)
public class CurrentBatteryV1Request extends Message { 
    public CurrentBatteryV1Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
    }

    
}