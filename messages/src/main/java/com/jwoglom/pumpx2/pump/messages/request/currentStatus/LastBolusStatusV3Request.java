package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBolusStatusV3Response;

@MessageProps(
    opCode=-70,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=LastBolusStatusV3Response.class
)
public class LastBolusStatusV3Request extends Message {
    public LastBolusStatusV3Request() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
    }
}
