package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.DoNotCall;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.ApiVersionDependent;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBatteryV1Response;

/**
 * Pump API version dependent request, do NOT invoke directly.
 * For API_V2_1
 *
 * @see com.jwoglom.pumpx2.pump.messages.builders.CurrentBatteryBuilder
 */
@MessageProps(
    opCode=52,
    size=0,
    type=MessageType.REQUEST,
    response=CurrentBatteryV1Response.class
)
@ApiVersionDependent
public class CurrentBatteryV1Request extends Message { 
    public CurrentBatteryV1Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
    }

    
}