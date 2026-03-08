package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentEgvGuiDataV2Response;

@MessageProps(
    opCode=-64,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=CurrentEgvGuiDataV2Response.class
)
public class CurrentEgvGuiDataV2Request extends Message {
    public CurrentEgvGuiDataV2Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
