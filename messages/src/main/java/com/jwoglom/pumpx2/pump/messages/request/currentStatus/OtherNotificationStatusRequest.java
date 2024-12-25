package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.OtherNotificationStatusResponse;

@MessageProps(
    opCode=-110,
    size=1,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=OtherNotificationStatusResponse.class,
    minApi=KnownApiVersion.MOBI_API_V3_5
)
public class OtherNotificationStatusRequest extends Message { 
    public OtherNotificationStatusRequest() {
        this.cargo = EMPTY;
    }

    public OtherNotificationStatusRequest(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) { 
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}