package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpVersionBRequest;

@MessageProps(
    opCode=-123,
    size=0,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=PumpVersionBRequest.class
)
public class PumpVersionBResponse extends Message {
    public PumpVersionBResponse() {}

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
