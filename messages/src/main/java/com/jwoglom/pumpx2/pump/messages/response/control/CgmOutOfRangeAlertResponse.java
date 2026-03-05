package com.jwoglom.pumpx2.pump.messages.response.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.control.CgmOutOfRangeAlertRequest;

@MessageProps(
    opCode=-57,
    size=0,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=CgmOutOfRangeAlertRequest.class,
    signed=true
)
public class CgmOutOfRangeAlertResponse extends Message {
    public CgmOutOfRangeAlertResponse() {}

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        this.cargo = raw;
    }
}
