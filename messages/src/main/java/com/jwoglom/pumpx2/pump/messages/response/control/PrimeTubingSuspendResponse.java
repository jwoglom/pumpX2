package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.PrimeTubingSuspendRequest;

/**
 * Response to a PrimeTubingSuspendRequest.
 *
 * Cargo layout (3 bytes, after HMAC removal):
 *   raw[0]: statusCode  (uint8, 0 = ACK/success; also referred to as bolusIdType in Tandem app model)
 *   raw[1]: unused padding
 *   raw[2]: reserve     (uint8)
 *
 * Derived from the decompiled Tandem Mobi Android app:
 *   PrimeTubingSuspendResponse$PrimeTubingSuspendCargo
 */
@MessageProps(
    opCode=-17,
    size=3,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=PrimeTubingSuspendRequest.class,
    signed=true
)
public class PrimeTubingSuspendResponse extends StatusMessage {

    private int statusCode;
    private int reserve;

    public PrimeTubingSuspendResponse() {}

    public PrimeTubingSuspendResponse(int statusCode, int reserve) {
        this.cargo = buildCargo(statusCode, reserve);
        this.statusCode = statusCode;
        this.reserve = reserve;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.statusCode = raw[0];
        // raw[1] is unused padding
        this.reserve = raw[2];
    }

    public static byte[] buildCargo(int statusCode, int reserve) {
        return Bytes.combine(
            new byte[]{ (byte) statusCode },
            new byte[]{ 0 },
            new byte[]{ (byte) reserve }
        );
    }

    /**
     * @return 0 if successful (ACK)
     */
    @Override
    public int getStatus() {
        return statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getReserve() {
        return reserve;
    }
}
