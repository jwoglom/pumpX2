package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.StreamDataReadinessRequest;

import org.apache.commons.lang3.Validate;

/**
 * Response to StreamDataReadinessRequest.
 *
 * Contains two fields packed into 2 bytes:
 *   byte[0] - FreestyleLibre2Readiness: readiness status for FSL2 sensor context transfer,
 *             mapped from a non-sequential set of numeric IDs used by the pump firmware.
 *   byte[1] - StreamDataType: the type of stream data requested (ordinal-based, 0 = FSL2
 *             sensor context, 1 = unknown).
 */
@MessageProps(
    opCode=-57,
    size=2,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    request=StreamDataReadinessRequest.class
)
public class StreamDataReadinessResponse extends Message {

    private int freestyleLibre2ReadinessId;
    private int streamDataTypeOrdinal;

    public StreamDataReadinessResponse() {}

    public StreamDataReadinessResponse(int freestyleLibre2ReadinessId, int streamDataTypeOrdinal) {
        this.cargo = buildCargo(freestyleLibre2ReadinessId, streamDataTypeOrdinal);
        this.freestyleLibre2ReadinessId = freestyleLibre2ReadinessId;
        this.streamDataTypeOrdinal = streamDataTypeOrdinal;
    }

    @Override
    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.freestyleLibre2ReadinessId = raw[0] & 0xFF;
        this.streamDataTypeOrdinal = raw[1] & 0xFF;
    }

    public static byte[] buildCargo(int freestyleLibre2ReadinessId, int streamDataTypeOrdinal) {
        return Bytes.combine(
            new byte[]{(byte) freestyleLibre2ReadinessId},
            new byte[]{(byte) streamDataTypeOrdinal}
        );
    }

    /**
     * Returns the raw numeric ID of the FreestyleLibre2Readiness status as reported by the pump.
     * The pump uses a non-sequential set of IDs: 0, 2, 5, 6, 8, 11 map to specific readiness
     * states; any other value indicates an unknown state.
     */
    public int getFreestyleLibre2ReadinessId() {
        return freestyleLibre2ReadinessId;
    }

    /**
     * Returns the FreestyleLibre2Readiness enum value corresponding to the raw ID from the pump.
     *
     * ID mapping (from Tandem Mobi Android app):
     *   0  -> PUMP_READY_FOR_FSL2_CONTEXT_TRANSFER
     *   2  -> NOT_READY_ANOTHER_CGM_SESSION_IS_ALREADY_ACTIVE
     *   5  -> NOT_READY_PUMP_TIME_IS_INVALID
     *   6  -> NOT_READY_PUMP_WORKFLOW_DOES_NOT_ALLOW_FSL2_START
     *   8  -> FREESTYLE_LIBRE_2_CGM_IS_UNAVAILABLE
     *   11 -> NOT_READY_THE_PUMP_IS_UNABLE_TO_FULFILL_THE_REQUEST_DUE_TO_AN_UNEXPECTED_ERROR
     *   other -> UNKNOWN
     */
    public FreestyleLibre2Readiness getFreestyleLibre2Readiness() {
        return FreestyleLibre2Readiness.fromId(freestyleLibre2ReadinessId);
    }

    /**
     * Returns the raw ordinal of the StreamDataType as reported by the pump.
     * 0 = FREESTYLE_LIBRE_2_SENSOR_CONTEXT, 1 = UNKNOWN.
     */
    public int getStreamDataTypeOrdinal() {
        return streamDataTypeOrdinal;
    }

    /**
     * Returns the StreamDataType enum value corresponding to the raw ordinal from the pump.
     */
    public StreamDataType getStreamDataType() {
        return StreamDataType.fromOrdinal(streamDataTypeOrdinal);
    }

    /**
     * Readiness status for a FreeStyle Libre 2 sensor context transfer.
     * The numeric IDs correspond to the values used by the Tandem pump firmware and are NOT
     * simple ordinals — they are sparse/non-sequential.
     */
    public enum FreestyleLibre2Readiness {
        PUMP_READY_FOR_FSL2_CONTEXT_TRANSFER(0),
        NOT_READY_ANOTHER_CGM_SESSION_IS_ALREADY_ACTIVE(2),
        NOT_READY_PUMP_TIME_IS_INVALID(5),
        NOT_READY_PUMP_WORKFLOW_DOES_NOT_ALLOW_FSL2_START(6),
        FREESTYLE_LIBRE_2_CGM_IS_UNAVAILABLE(8),
        NOT_READY_THE_PUMP_IS_UNABLE_TO_FULFILL_THE_REQUEST_DUE_TO_AN_UNEXPECTED_ERROR(11),
        UNKNOWN(-1),
        ;

        private final int id;

        FreestyleLibre2Readiness(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static FreestyleLibre2Readiness fromId(int id) {
            for (FreestyleLibre2Readiness r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * The type of streaming data being queried for readiness.
     * Ordinal-based: 0 = FREESTYLE_LIBRE_2_SENSOR_CONTEXT, 1 = UNKNOWN.
     */
    public enum StreamDataType {
        FREESTYLE_LIBRE_2_SENSOR_CONTEXT(0),
        UNKNOWN(1),
        ;

        private final int ordinalValue;

        StreamDataType(int ordinalValue) {
            this.ordinalValue = ordinalValue;
        }

        public int ordinalValue() {
            return ordinalValue;
        }

        public static StreamDataType fromOrdinal(int ordinal) {
            for (StreamDataType t : values()) {
                if (t.ordinalValue == ordinal) {
                    return t;
                }
            }
            return UNKNOWN;
        }
    }
}
