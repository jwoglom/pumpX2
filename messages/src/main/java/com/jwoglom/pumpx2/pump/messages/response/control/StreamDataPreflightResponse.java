package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.StreamDataPreflightRequest;

/**
 * Response to a StreamDataPreflightRequest.
 *
 * Cargo layout (3 bytes, after HMAC removal):
 *   raw[0]: status       (uint8, 0 = ACK/success)
 *   raw[1]: statusTypeId (uint8, PreflightStatusType ordinal)
 *   raw[2]: streamTypeId (uint8, PreflightStreamType ordinal)
 *
 * Derived from the decompiled Tandem Mobi Android app:
 *   StreamDataPreflightResponse$StreamDataPreflightCargo (C2884c cases 25 and 26)
 */
@MessageProps(
    opCode=-125,
    size=3,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=StreamDataPreflightRequest.class,
    minApi=KnownApiVersion.API_FUTURE,
    signed=true
)
public class StreamDataPreflightResponse extends StatusMessage {

    private int status;
    private int statusTypeId;
    private int streamTypeId;

    public StreamDataPreflightResponse() {}

    public StreamDataPreflightResponse(int status, int statusTypeId, int streamTypeId) {
        this.cargo = buildCargo(status, statusTypeId, streamTypeId);
        this.status = status;
        this.statusTypeId = statusTypeId;
        this.streamTypeId = streamTypeId;
    }

    @Override
    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.statusTypeId = raw[1] & 0xFF;
        this.streamTypeId = raw[2] & 0xFF;
    }

    public static byte[] buildCargo(int status, int statusTypeId, int streamTypeId) {
        return Bytes.combine(
            new byte[]{ (byte) status },
            new byte[]{ (byte) statusTypeId },
            new byte[]{ (byte) streamTypeId }
        );
    }

    @Override
    public int getStatus() {
        return status;
    }

    public int getStatusTypeId() {
        return statusTypeId;
    }

    public PreflightStatusType getStatusType() {
        return PreflightStatusType.fromId(statusTypeId);
    }

    public int getStreamTypeId() {
        return streamTypeId;
    }

    public PreflightStreamType getStreamType() {
        return PreflightStreamType.fromId(streamTypeId);
    }

    public enum PreflightStatusType {
        SUCCESS(0),
        SENSOR_CONTEXT_TOO_BIG(1),
        ANOTHER_CGM_ACTIVE(2),
        INCOMPATIBLE_SENSOR(3),
        INVALID_SENSOR_CONTEXT(4),
        INVALID_PUMP_TIME(5),
        PUMP_IS_BUSY(6),
        SENSOR_HAS_ALREADY_BEEN_USED(7),
        INTERNAL_ERROR(8),
        ENGINEERING_USE_ONLY(9),
        TIMEOUT(10),
        SENSOR_UNAVAILABLE(11),
        INVALID_SENSOR_TIME_PARAMS(12),
        UNKNOWN(13),
        ;

        private final int id;

        PreflightStatusType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static PreflightStatusType fromId(int id) {
            for (PreflightStatusType t : values()) {
                if (t.id == id) return t;
            }
            return UNKNOWN;
        }
    }

    public enum PreflightStreamType {
        FREESTYLE_LIBRE_2(0),
        UNKNOWN(1),
        ;

        private final int id;

        PreflightStreamType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static PreflightStreamType fromId(int id) {
            for (PreflightStreamType t : values()) {
                if (t.id == id) return t;
            }
            return UNKNOWN;
        }
    }
}
