package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.SetLowInsulinAlertRequest;

/**
 * Response to a SetLowInsulinAlertRequest.
 *
 * Cargo layout (1 byte, after HMAC removal):
 *   raw[0]: status (uint8, 0=ACK, 1=NACK, 2=UNKNOWN)
 *
 * Derived from the decompiled Tandem Mobi Android app:
 *   ResponseStatusCargo (status-only response, opcode 0xDF)
 */
@MessageProps(
    opCode=-33,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=SetLowInsulinAlertRequest.class,
    signed=true
)
public class SetLowInsulinAlertResponse extends StatusMessage {

    private int status;

    public SetLowInsulinAlertResponse() {}

    public SetLowInsulinAlertResponse(int status) {
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
        return Bytes.combine(
            new byte[]{ (byte) status });
    }

    /**
     * @return 0=ACK (success), 1=NACK (rejected), 2=UNKNOWN
     */
    @Override
    public int getStatus() {
        return status;
    }
}
