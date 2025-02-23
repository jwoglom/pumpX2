package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.UnknownMobiOpcode20Response;

/**
 * I think this might be sent by the app whenever a user uses face id to authenticate,
 * but I'm not sure and don't know if it's required
 */
@MessageProps(
    opCode=20,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=UnknownMobiOpcode20Response.class
)
public class UnknownMobiOpcode20Request extends Message { 
    public UnknownMobiOpcode20Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) { 
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}