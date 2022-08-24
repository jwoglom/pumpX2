package com.jwoglom.pumpx2.pump.messages.request.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.SuspendPumpingResponse;

@MessageProps(
    opCode=-100,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=SuspendPumpingResponse.class,
    signed=true,
    minApi=KnownApiVersion.API_FUTURE
)
public class SuspendPumpingRequest extends Message { 
    public SuspendPumpingRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}