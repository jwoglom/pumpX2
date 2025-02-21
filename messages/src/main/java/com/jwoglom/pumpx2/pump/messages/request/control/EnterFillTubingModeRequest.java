package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.EnterFillTubingModeResponse;

@MessageProps(
    opCode=-108,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    modifiesInsulinDelivery=true,
    response=EnterFillTubingModeResponse.class
)
public class EnterFillTubingModeRequest extends Message { 
    public EnterFillTubingModeRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}