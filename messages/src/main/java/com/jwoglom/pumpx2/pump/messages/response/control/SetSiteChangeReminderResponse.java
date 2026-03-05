package com.jwoglom.pumpx2.pump.messages.response.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.control.SetSiteChangeReminderRequest;

@MessageProps(
    opCode=-35,
    size=0,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=SetSiteChangeReminderRequest.class,
    signed=true
)
public class SetSiteChangeReminderResponse extends Message {
    public SetSiteChangeReminderResponse() {}

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        this.cargo = raw;
    }
}
