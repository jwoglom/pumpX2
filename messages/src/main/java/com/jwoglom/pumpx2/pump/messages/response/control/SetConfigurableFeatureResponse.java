package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.SetConfigurableFeatureRequest;

/**
 * Response to a {@link SetConfigurableFeatureRequest}.
 *
 * Cargo layout (2 bytes, after HMAC removal):
 *   raw[0]: status (uint8, 0 = success)
 *   raw[1]: reason (uint8)
 *
 * Derived from the decompiled t:connect Android app (SetConfigurableFeatureResponse, opcode 117).
 */
@MessageProps(
    opCode=117,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=SetConfigurableFeatureRequest.class,
    signed=true
)
public class SetConfigurableFeatureResponse extends StatusMessage {

    private int status;
    private int reason;

    public SetConfigurableFeatureResponse() {}

    public SetConfigurableFeatureResponse(int status, int reason) {
        this.cargo = buildCargo(status, reason);
        this.status = status;
        this.reason = reason;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.reason = raw[1] & 0xFF;
    }

    public static byte[] buildCargo(int status, int reason) {
        return Bytes.combine(
            new byte[]{ (byte) status, (byte) reason });
    }

    /**
     * @return 0 if successful
     */
    @Override
    public int getStatus() {
        return status;
    }

    public int getReason() {
        return reason;
    }
}
