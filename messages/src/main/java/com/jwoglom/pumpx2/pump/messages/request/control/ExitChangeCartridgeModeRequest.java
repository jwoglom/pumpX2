package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.ExitChangeCartridgeModeResponse;

/**
 * Exits the change cartridge mode. Called after EnterChangeCartridgeModeRequest
 * once the new cartridge has been inserted into the pump.
 *
 * Precondition: Pumping must be suspended and must currently be in EnterChangeCartridgeMode
 */
@MessageProps(
    opCode=-110,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=ExitChangeCartridgeModeResponse.class
)
public class ExitChangeCartridgeModeRequest extends Message { 
    public ExitChangeCartridgeModeRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) { 
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}