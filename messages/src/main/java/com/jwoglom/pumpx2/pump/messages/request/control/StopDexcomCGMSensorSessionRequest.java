package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.StopDexcomCGMSensorSessionResponse;

/**
 * Stop either G6 or G7 active CGM sensor session.
 */
@MessageProps(
    opCode=-76,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=StopDexcomCGMSensorSessionResponse.class
)
public class StopDexcomCGMSensorSessionRequest extends Message {
    public StopDexcomCGMSensorSessionRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}