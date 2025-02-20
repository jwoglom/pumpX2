package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentEGVGuiDataResponse;

/**
 * Gets the current CGM reading
 */
@MessageProps(
    opCode=34,
    size=0,
    type=MessageType.REQUEST,
    response=CurrentEGVGuiDataResponse.class
)
public class CurrentEGVGuiDataRequest extends Message { 
    public CurrentEGVGuiDataRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}