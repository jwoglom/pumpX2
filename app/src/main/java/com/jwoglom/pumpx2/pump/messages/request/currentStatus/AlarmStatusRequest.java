package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;

@MessageProps(
    opCode=70,
    size=0,
    type= MessageType.REQUEST,
    response= AlarmStatusResponse.class
)
public class AlarmStatusRequest extends Message {
    public AlarmStatusRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
