package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BolusPermissionChangeReasonResponse;

/**
 * TODO: causes an ErrorResponse!
 */
@MessageProps(
    opCode=-88,
    size=0,
    type=MessageType.REQUEST,
    response=BolusPermissionChangeReasonResponse.class,
    minApi=KnownApiVersion.API_FUTURE
)
public class BolusPermissionChangeReasonRequest extends Message { 
    public BolusPermissionChangeReasonRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}