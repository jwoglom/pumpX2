package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.SetRuntimeConfigRequest;

/**
 * The pump's reply to a {@link SetRuntimeConfigRequest}, reporting whether the value
 * was accepted.
 *
 * Cargo layout (1 byte):
 *   raw[0]: status (uint8, 0 = success)
 */
@MessageProps(
    opCode=123,
    size=1,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=SetRuntimeConfigRequest.class,
    signed=true
)
public class SetRuntimeConfigResponse extends StatusMessage {

    private int status;

    public SetRuntimeConfigResponse() {}

    public SetRuntimeConfigResponse(int status) {
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
     * @return 0 if successful
     */
    @Override
    public int getStatus() {
        return status;
    }
}
