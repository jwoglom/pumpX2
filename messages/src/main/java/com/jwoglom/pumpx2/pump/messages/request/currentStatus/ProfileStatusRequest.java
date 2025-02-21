package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ProfileStatusResponse;

/**
 * Gets general information on insulin profiles.
 * @see com.jwoglom.pumpx2.pump.messages.builders.InsulinDeliveryProfileRequestBuilder
 */
@MessageProps(
    opCode=62,
    size=0,
    type=MessageType.REQUEST,
    response=ProfileStatusResponse.class
)
public class ProfileStatusRequest extends Message { 
    public ProfileStatusRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}