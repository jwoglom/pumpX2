package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.GlobalMaxBolusSettingsResponse;

@MessageProps(
    opCode=-116,
    size=0,
    type=MessageType.REQUEST,
    response=GlobalMaxBolusSettingsResponse.class
)
public class GlobalMaxBolusSettingsRequest extends Message { 
    public GlobalMaxBolusSettingsRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}