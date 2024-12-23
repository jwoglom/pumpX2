package com.jwoglom.pumpx2.pump.messages.response.control;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.control.ChangeControlIQSettingsRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-53,
    size=3,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request=ChangeControlIQSettingsRequest.class
)
public class ChangeControlIQSettingsResponse extends Message {
    
    
    public ChangeControlIQSettingsResponse() {
        this.cargo = EMPTY;
    }

    public ChangeControlIQSettingsResponse(byte[] raw) {
        parse(raw);
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        
    }

    
    
}