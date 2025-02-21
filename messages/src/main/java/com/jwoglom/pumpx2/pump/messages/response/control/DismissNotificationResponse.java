package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.DismissNotificationRequest;

import java.math.BigInteger;

@MessageProps(
    opCode=-71,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    signed=true,
    request=DismissNotificationRequest.class
)
public class DismissNotificationResponse extends StatusMessage {
    private int status;

    public DismissNotificationResponse() {
        this.cargo = EMPTY;

    }

    public DismissNotificationResponse(int status) {
        this.cargo = buildCargo(status);
        this.status = status;

    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];

    }

    public static byte[] buildCargo(int status) {
        return Bytes.combine(
                new byte[]{ (byte) status });
    }

    /**
     * @return 0 if successful
     */
    public int getStatus() {
        return status;
    }
}