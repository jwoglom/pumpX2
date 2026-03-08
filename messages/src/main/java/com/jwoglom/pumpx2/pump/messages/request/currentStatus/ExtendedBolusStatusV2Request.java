package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ExtendedBolusStatusV2Response;

@MessageProps(
    opCode=-74,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=ExtendedBolusStatusV2Response.class
)
public class ExtendedBolusStatusV2Request extends Message {
    public ExtendedBolusStatusV2Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
