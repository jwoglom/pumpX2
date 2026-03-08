package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.SetBgReminderRequest;

@MessageProps(
    opCode=-39,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=SetBgReminderRequest.class,
    signed=true
)
public class SetBgReminderResponse extends StatusMessage {
    private int status;

    public SetBgReminderResponse() {}

    public SetBgReminderResponse(int status) {
        this.cargo = buildCargo(status);
        this.status = status;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
    }

    public static byte[] buildCargo(int status) {
        return new byte[]{ (byte) status };
    }

    @Override
    public int getStatus() {
        return status;
    }
}
