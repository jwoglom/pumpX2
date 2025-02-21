package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMOORAlertSettingsResponse;

@MessageProps(
    opCode=94,
    size=0,
    type=MessageType.REQUEST,
    response=CGMOORAlertSettingsResponse.class
)
public class CGMOORAlertSettingsRequest extends Message { 
    public CGMOORAlertSettingsRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}