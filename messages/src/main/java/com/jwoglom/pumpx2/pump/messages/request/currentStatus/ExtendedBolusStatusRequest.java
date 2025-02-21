package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ExtendedBolusStatusResponse;

@MessageProps(
    opCode=46,
    size=0,
    type=MessageType.REQUEST,
    response=ExtendedBolusStatusResponse.class
)
public class ExtendedBolusStatusRequest extends Message { 
    public ExtendedBolusStatusRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}