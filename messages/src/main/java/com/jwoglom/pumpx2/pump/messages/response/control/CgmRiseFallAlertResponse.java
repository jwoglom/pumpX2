package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.CgmRiseFallAlertRequest;

@MessageProps(
    opCode=-59,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=CgmRiseFallAlertRequest.class,
    signed=true
)
public class CgmRiseFallAlertResponse extends StatusMessage {
    private int status;

    public CgmRiseFallAlertResponse() {}

    public CgmRiseFallAlertResponse(int status) {
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
