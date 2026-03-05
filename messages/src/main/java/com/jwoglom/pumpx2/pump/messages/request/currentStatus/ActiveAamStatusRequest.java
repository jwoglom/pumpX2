package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ActiveAamStatusResponse;

@MessageProps(
    opCode=-110,
    size=1,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CURRENT_STATUS,
    response=ActiveAamStatusResponse.class,
    minApi=KnownApiVersion.MOBI_API_V3_5
)
public class ActiveAamStatusRequest extends Message {
    public ActiveAamStatusRequest() {
        this.cargo = EMPTY;
    }

    public ActiveAamStatusRequest(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;

    }


}
