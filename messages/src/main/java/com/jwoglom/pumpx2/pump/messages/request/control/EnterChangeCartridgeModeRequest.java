package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.EnterChangeCartridgeModeResponse;

/**
 * Enters change cartridge mode.
 *
 * Precondition: insulin must be suspended.
 */
@MessageProps(
    opCode=-112,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    modifiesInsulinDelivery=true,
    response=EnterChangeCartridgeModeResponse.class
)
public class EnterChangeCartridgeModeRequest extends Message {
    public EnterChangeCartridgeModeRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}