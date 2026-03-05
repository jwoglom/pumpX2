package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.CgmOutOfRangeAlertResponse;

@MessageProps(
    opCode=-58,
    size=0,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=CgmOutOfRangeAlertResponse.class,
    signed=true
)
public class CgmOutOfRangeAlertRequest extends Message {
    public CgmOutOfRangeAlertRequest() {
        this.cargo = EMPTY;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        this.cargo = raw;
    }
}
