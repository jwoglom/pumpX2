package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentActiveIdpValuesRequest;

@MessageProps(
    opCode=-105,
    size=0,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=CurrentActiveIdpValuesRequest.class
)
public class CurrentActiveIdpValuesResponse extends Message {
    public CurrentActiveIdpValuesResponse() {}

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
