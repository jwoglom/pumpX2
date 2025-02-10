package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.OtherNotification2StatusResponse;

@MessageProps(
    opCode=118,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response= OtherNotification2StatusResponse.class
)
public class OtherNotification2StatusRequest extends Message {
    public OtherNotification2StatusRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) { 
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}