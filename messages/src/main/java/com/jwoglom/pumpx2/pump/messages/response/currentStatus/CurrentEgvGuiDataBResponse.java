package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentEgvGuiDataBRequest;

@MessageProps(
    opCode=-63,
    size=0,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=CurrentEgvGuiDataBRequest.class
)
public class CurrentEgvGuiDataBResponse extends Message {
    public CurrentEgvGuiDataBResponse() {}

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
