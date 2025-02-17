package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMStatusRequest;

import java.time.Instant;

@MessageProps(
    opCode=81,
    size=10,
    type=MessageType.RESPONSE,
    request=CGMStatusRequest.class
)
public class CGMStatusResponse extends Message {
    
    private int sessionStateId;
    private SessionState sessionState;
    private long lastCalibrationTimestamp;
    private long sensorStartedTimestamp;
    private int transmitterBatteryStatusId;
    private TransmitterBatteryStatus transmitterBatteryStatus;
    
    public CGMStatusResponse() {}
    
    public CGMStatusResponse(int sessionStateId, long lastCalibrationTimestamp, long sensorStartedTimestamp, int transmitterBatteryStatusId) {
        this.cargo = buildCargo(sessionStateId, lastCalibrationTimestamp, sensorStartedTimestamp, transmitterBatteryStatusId);
        parse(cargo);
        
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        this.cargo = raw;
        this.sessionStateId = raw[0];
        this.sessionState = getSessionState();
        this.lastCalibrationTimestamp = Bytes.readUint32(raw, 1);
        this.sensorStartedTimestamp = Bytes.readUint32(raw, 5);
        this.transmitterBatteryStatusId = raw[9];
        this.transmitterBatteryStatus = getTransmitterBatteryStatus();
        
    }

    
    public static byte[] buildCargo(int sessionState, long lastCalibrationTimestamp, long sensorStartedTimestamp, int transmitterBatteryStatus) {
        return Bytes.combine(
            new byte[]{ (byte) sessionState }, 
            Bytes.toUint32(lastCalibrationTimestamp), 
            Bytes.toUint32(sensorStartedTimestamp), 
            new byte[]{ (byte) transmitterBatteryStatus });
    }
    
    public int getSessionStateId() {
        return sessionStateId;
    }

    /**
     * @return the state of the Dexcom CGM session
     */
    public SessionState getSessionState() {
        return SessionState.fromId(sessionStateId);
    }

    public enum SessionState {
        SESSION_STOPPED(0),
        SESSION_START_PENDING(1),
        SESSION_ACTIVE(2),
        SESSION_STOP_PENDING(3),
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
            return null;
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
     * @return the battery status of the Dexcom CGM transmitter
     */
    public TransmitterBatteryStatus getTransmitterBatteryStatus() {
        return TransmitterBatteryStatus.fromId(transmitterBatteryStatusId);
    }
    public enum TransmitterBatteryStatus {
        UNAVAILABLE(0),
        ERROR(1),
        EXPIRED(2),
        OK(3),
        OUT_OF_RANGE(4),
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
            return null;
        }
    }
}