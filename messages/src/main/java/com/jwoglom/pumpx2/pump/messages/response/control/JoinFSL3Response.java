package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.JoinFSL3Request;

/**
 * Response to a {@link JoinFSL3Request}.
 *
 * Cargo layout (2 bytes, after HMAC removal):
 *   raw[0]: status     (uint8, 0 = success)
 *   raw[1]: statusType (uint8)
 *
 * Derived from the decompiled t:connect Android app (JoinFSL3Response, opcode 121).
 */
@MessageProps(
    opCode=121,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=JoinFSL3Request.class,
    signed=true
)
public class JoinFSL3Response extends StatusMessage {

    private int status;
    private int statusType;

    public JoinFSL3Response() {}

    public JoinFSL3Response(int status, int statusType) {
        this.cargo = buildCargo(status, statusType);
        this.status = status;
        this.statusType = statusType;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.statusType = raw[1] & 0xFF;
    }

    public static byte[] buildCargo(int status, int statusType) {
        return Bytes.combine(
            new byte[]{ (byte) status, (byte) statusType });
    }

    /**
     * @return 0 if successful
     */
    @Override
    public int getStatus() {
        return status;
    }

    public int getStatusType() {
        return statusType;
    }
}
