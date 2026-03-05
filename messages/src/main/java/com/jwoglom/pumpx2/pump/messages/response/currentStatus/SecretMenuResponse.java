package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.SecretMenuRequest;

@MessageProps(
    opCode=-67,
    size=0,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=SecretMenuRequest.class
)
public class SecretMenuResponse extends Message {
    public SecretMenuResponse() {}

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
