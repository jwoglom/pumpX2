package com.jwoglom.pumpx2.pump.messages.response.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.StatusMessage;
import com.jwoglom.pumpx2.pump.messages.request.control.SetSensorTypeRequest;

/**
 * Response to a SetSensorTypeRequest (SetCGMSensorType in Tandem app).
 *
 * Cargo layout (2 bytes, after HMAC removal):
 *   raw[0]: status                (uint8, ResponseStatus: 0=ACK, 1=NACK)
 *   raw[1]: statusAcknowledgement (uint8, StatusAcknowledgement ordinal)
 *
 * Derived from the decompiled Tandem Mobi Android app:
 *   SetCGMSensorTypeResponse (C2884c case 24)
 */
@MessageProps(
    opCode=-63,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CONTROL,
    request=SetSensorTypeRequest.class,
    signed=true
)
public class SetSensorTypeResponse extends StatusMessage {

    private int status;
    private int statusAcknowledgement;

    public SetSensorTypeResponse() {}

    public SetSensorTypeResponse(int status, int statusAcknowledgement) {
        this.cargo = buildCargo(status, statusAcknowledgement);
        this.status = status;
        this.statusAcknowledgement = statusAcknowledgement;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.status = raw[0];
        this.statusAcknowledgement = raw[1] & 0xFF;
    }

    public static byte[] buildCargo(int status, int statusAcknowledgement) {
        return Bytes.combine(
            new byte[]{ (byte) status, (byte) statusAcknowledgement });
    }

    /**
     * @return 0=ACK, 1=NACK
     */
    @Override
    public int getStatus() {
        return status;
    }

    public int getStatusAcknowledgement() {
        return statusAcknowledgement;
    }

    public StatusAcknowledgement getStatusAcknowledgementType() {
        return StatusAcknowledgement.fromCode(statusAcknowledgement);
    }

    /**
     * Status acknowledgement codes from the Tandem app's StatusAcknowledgement enum.
     */
    public enum StatusAcknowledgement {
        SUCCESS(0),
        CGM_SESSION_ALREADY_ACTIVE(1),
        INVALID_CGM_TYPE(2),
        MALFUNCTION_ACTIVE(3),
        MISSING_OR_INVALID_CGM_SUPPORT_PACKAGE(4),
        INVALID_SCREEN_STATE(5),
        UNKNOWN(6),
        ;

        private final int code;

        StatusAcknowledgement(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static StatusAcknowledgement fromCode(int code) {
            for (StatusAcknowledgement s : values()) {
                if (s.code == code) return s;
            }
            return UNKNOWN;
        }
    }
}
