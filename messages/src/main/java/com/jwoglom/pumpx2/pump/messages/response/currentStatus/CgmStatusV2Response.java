package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.models.SupportedDevices;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CgmStatusV2Request;

import java.time.Instant;

/**
 * Returns CGM (Continuous Glucose Monitor) status for Mobi pumps.
 *
 * Binary layout (20 bytes total):
 *   offset 0  (1 byte):  sessionState          - CGMStatusBState ordinal
 *   offset 1  (4 bytes): lastCalibrationTimestamp - uint32, pump epoch seconds
 *   offset 5  (4 bytes): sensorStartedTimestamp   - uint32, pump epoch seconds
 *   offset 9  (1 byte):  transmitterBatteryStatus - TransmitterBatteryStatus ordinal
 *   offset 10 (4 bytes): sessionDuration          - uint32, seconds
 *   offset 14 (4 bytes): sessionTimeRemaining     - uint32, seconds
 *   offset 18 (1 byte):  cgmSensorType            - CGMSensorType ordinal
 *   offset 19 (1 byte):  gracePeriod              - nonzero = true
 */
@MessageProps(
    opCode=-65,
    size=20,
    type=MessageType.RESPONSE,
    characteristic=Characteristic.CURRENT_STATUS,
    minApi=KnownApiVersion.MOBI_API_V3_5,
    supportedDevices=SupportedDevices.MOBI_ONLY,
    request=CgmStatusV2Request.class
)
public class CgmStatusV2Response extends Message {

    private int sessionStateId;
    private SessionState sessionState;
    private long lastCalibrationTimestamp;
    private long sensorStartedTimestamp;
    private int transmitterBatteryStatusId;
    private TransmitterBatteryStatus transmitterBatteryStatus;
    private long sessionDurationSeconds;
    private long sessionTimeRemainingSeconds;
    private int cgmSensorTypeId;
    private CgmSensorType cgmSensorType;
    private boolean gracePeriod;

    public CgmStatusV2Response() {
    }

    public CgmStatusV2Response(
            int sessionStateId,
            long lastCalibrationTimestamp,
            long sensorStartedTimestamp,
            int transmitterBatteryStatusId,
            long sessionDurationSeconds,
            long sessionTimeRemainingSeconds,
            int cgmSensorTypeId,
            boolean gracePeriod) {
        this.cargo = buildCargo(
                sessionStateId,
                lastCalibrationTimestamp,
                sensorStartedTimestamp,
                transmitterBatteryStatusId,
                sessionDurationSeconds,
                sessionTimeRemainingSeconds,
                cgmSensorTypeId,
                gracePeriod);
        parse(this.cargo);
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.sessionStateId = raw[0] & 0xFF;
        this.sessionState = getSessionState();
        this.lastCalibrationTimestamp = Bytes.readUint32(raw, 1);
        this.sensorStartedTimestamp = Bytes.readUint32(raw, 5);
        this.transmitterBatteryStatusId = raw[9] & 0xFF;
        this.transmitterBatteryStatus = getTransmitterBatteryStatus();
        this.sessionDurationSeconds = Bytes.readUint32(raw, 10);
        this.sessionTimeRemainingSeconds = Bytes.readUint32(raw, 14);
        this.cgmSensorTypeId = raw[18] & 0xFF;
        this.cgmSensorType = getCgmSensorType();
        this.gracePeriod = (raw[19] & 0xFF) != 0;
    }

    public static byte[] buildCargo(
            int sessionStateId,
            long lastCalibrationTimestamp,
            long sensorStartedTimestamp,
            int transmitterBatteryStatusId,
            long sessionDurationSeconds,
            long sessionTimeRemainingSeconds,
            int cgmSensorTypeId,
            boolean gracePeriod) {
        return Bytes.combine(
                new byte[]{(byte) sessionStateId},
                Bytes.toUint32(lastCalibrationTimestamp),
                Bytes.toUint32(sensorStartedTimestamp),
                new byte[]{(byte) transmitterBatteryStatusId},
                Bytes.toUint32(sessionDurationSeconds),
                Bytes.toUint32(sessionTimeRemainingSeconds),
                new byte[]{(byte) cgmSensorTypeId},
                new byte[]{(byte) (gracePeriod ? 1 : 0)});
    }

    public int getSessionStateId() {
        return sessionStateId;
    }

    /**
     * @return the state of the CGM session
     */
    public SessionState getSessionState() {
        return SessionState.fromId(sessionStateId);
    }

    /**
     * CGM session states matching the Tandem app's CGMStatusBState enum ordinals.
     */
    public enum SessionState {
        STATUS_STOPPED(0),
        STATUS_START_PENDING(1),
        SESSION_ACTIVE(2),
        SESSION_STOP_PENDING(3),
        UNKNOWN(4),
        ;

        private final int id;
        SessionState(int id) {
            this.id = id;
        }

        public static SessionState fromId(int id) {
            for (SessionState s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return UNKNOWN;
        }
    }

    public long getLastCalibrationTimestamp() {
        return lastCalibrationTimestamp;
    }

    public Instant getLastCalibrationTimestampInstant() {
        return Dates.fromJan12008EpochSecondsToDate(lastCalibrationTimestamp);
    }

    public long getSensorStartedTimestamp() {
        return sensorStartedTimestamp;
    }

    public Instant getSensorStartedTimestampInstant() {
        return Dates.fromJan12008EpochSecondsToDate(sensorStartedTimestamp);
    }

    public int getTransmitterBatteryStatusId() {
        return transmitterBatteryStatusId;
    }

    /**
     * @return the battery status of the CGM transmitter.
     * Ordinals match the Tandem app's TransmitterBatteryStatus enum:
     * 0=UNAVAILABLE, 1=ERROR, 2=EXPIRED, 3=OKAY, 4=OUT_OF_RANGE, 5=UNKNOWN
     */
    public TransmitterBatteryStatus getTransmitterBatteryStatus() {
        return TransmitterBatteryStatus.fromId(transmitterBatteryStatusId);
    }

    /**
     * TransmitterBatteryStatus ordinals match the Tandem app's enum, which differs slightly
     * from the CGMStatusResponse variant (uses "OKAY" instead of "OK").
     */
    public enum TransmitterBatteryStatus {
        UNAVAILABLE(0),
        ERROR(1),
        EXPIRED(2),
        OKAY(3),
        OUT_OF_RANGE(4),
        UNKNOWN(5),
        ;

        private final int id;
        TransmitterBatteryStatus(int id) {
            this.id = id;
        }

        public static TransmitterBatteryStatus fromId(int id) {
            for (TransmitterBatteryStatus s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * @return the duration of the current CGM session, in seconds
     */
    public long getSessionDurationSeconds() {
        return sessionDurationSeconds;
    }

    /**
     * @return the time remaining in the current CGM session, in seconds
     */
    public long getSessionTimeRemainingSeconds() {
        return sessionTimeRemainingSeconds;
    }

    public int getCgmSensorTypeId() {
        return cgmSensorTypeId;
    }

    /**
     * @return the type of CGM sensor in use
     */
    public CgmSensorType getCgmSensorType() {
        return CgmSensorType.fromId(cgmSensorTypeId);
    }

    /**
     * CGM sensor types matching the Tandem app's CGMSensorType enum ordinals.
     */
    public enum CgmSensorType {
        NOT_APPLICABLE(0),
        DEXCOM_G6(1),
        FSL2(2),
        DEXCOM_G7(3),
        UNKNOWN(4),
        ;

        private final int id;
        CgmSensorType(int id) {
            this.id = id;
        }

        public static CgmSensorType fromId(int id) {
            for (CgmSensorType s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * @return true if the CGM session is in a grace period
     */
    public boolean isGracePeriod() {
        return gracePeriod;
    }
}
