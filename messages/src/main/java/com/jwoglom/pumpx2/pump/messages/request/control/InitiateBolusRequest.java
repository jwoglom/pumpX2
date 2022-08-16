package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.InitiateBolusResponse;

@MessageProps(
    opCode=-98,
    size=61,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=InitiateBolusResponse.class,
    signed=true
)
public class InitiateBolusRequest extends Message { 
    public InitiateBolusRequest() {
        //this.cargo = EMPTY;
        this.cargo = new byte[props().size()];
    }

    public InitiateBolusRequest(byte[] raw) {
        this.cargo = raw;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}