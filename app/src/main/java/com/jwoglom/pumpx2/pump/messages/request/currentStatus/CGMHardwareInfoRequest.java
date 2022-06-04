package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMHardwareInfoResponse;

@MessageProps(
        opCode=96,
        size=0,
        type= MessageType.REQUEST,
        response= CGMHardwareInfoResponse.class
)
public class CGMHardwareInfoRequest extends Message {
    public CGMHardwareInfoRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
