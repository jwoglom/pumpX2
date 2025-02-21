package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CommonSoftwareInfoResponse;

/**
 * TODO: size may always be 1 and not 0, collect more samples
 */
@MessageProps(
    opCode=-114,
    size=0, // OR 1
    variableSize=true,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=CommonSoftwareInfoResponse.class
)
public class CommonSoftwareInfoRequest extends Message { 
    public CommonSoftwareInfoRequest() {
        this.cargo = EMPTY;
    }
    public CommonSoftwareInfoRequest(byte[] raw) {
        this.cargo = raw;
    }

    public void parse(byte[] raw) {
        //Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        
    }

    
}