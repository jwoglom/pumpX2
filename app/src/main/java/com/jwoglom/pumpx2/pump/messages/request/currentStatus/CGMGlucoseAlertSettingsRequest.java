package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMGlucoseAlertSettingsResponse;

@MessageProps(
    opCode=90,
    size=0,
    type=MessageType.REQUEST,
    response=CGMGlucoseAlertSettingsResponse.class
)
public class CGMGlucoseAlertSettingsRequest extends Message { 
    public CGMGlucoseAlertSettingsRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}