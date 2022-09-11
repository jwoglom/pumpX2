package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.builders.CurrentBatteryRequestBuilder;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBatteryV2Response;

/**
 * API Version dependent. Do NOT invoke directly.
 *
 * @see CurrentBatteryRequestBuilder
 */
@MessageProps(
    opCode=-112,
    size=0,
    type=MessageType.REQUEST,
    response=CurrentBatteryV2Response.class,
    minApi=KnownApiVersion.API_V2_5
)
public class CurrentBatteryV2Request extends Message { 
    public CurrentBatteryV2Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
    }

    
}