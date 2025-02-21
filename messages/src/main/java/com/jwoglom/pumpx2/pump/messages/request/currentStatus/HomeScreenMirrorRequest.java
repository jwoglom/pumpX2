package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.HomeScreenMirrorResponse;

/**
 * Provides basic information which is provided on the pump home screen, such as current CGM status,
 * bolus and basal status icons, and remaining insulin amount.
 */
@MessageProps(
    opCode=56,
    size=0,
    type=MessageType.REQUEST,
    response=HomeScreenMirrorResponse.class
)
public class HomeScreenMirrorRequest extends Message { 
    public HomeScreenMirrorRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}