package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.GetSavedG7PairingCodeResponse;

import org.apache.commons.lang3.Validate;

@MessageProps(
    opCode=116,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=GetSavedG7PairingCodeResponse.class
)
public class GetSavedG7PairingCodeRequest extends Message {
    public GetSavedG7PairingCodeRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) { 
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
    }
}