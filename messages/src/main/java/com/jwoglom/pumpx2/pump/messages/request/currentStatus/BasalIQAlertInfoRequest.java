package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BasalIQAlertInfoResponse;

/**
 * On a non-BasalIQ pump returns BAD_OPCODE
 */
@MessageProps(
    opCode=102,
    size=0,
    type=MessageType.REQUEST,
    response=BasalIQAlertInfoResponse.class
)
public class BasalIQAlertInfoRequest extends Message { 
    public BasalIQAlertInfoRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}