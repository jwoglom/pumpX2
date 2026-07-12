package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 214,
    displayName = "CGM Stop Session GX",
    internalName = "LID_CGM_STOP_SESSION_GX"
)
public class CgmStopSessionHistoryLog extends HistoryLog {

    private long currentTransmitterTime;
    private long sessionStartTime;
    private long sessionStopTime;
    private int sessionStopReasonRaw;
    private int sessionDuration;

    public CgmStopSessionHistoryLog() {}
    public CgmStopSessionHistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0);
    }

    public CgmStopSessionHistoryLog(long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int sessionStopReasonRaw, int sessionDuration) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentTransmitterTime, sessionStartTime, sessionStopTime, sessionStopReasonRaw, sessionDuration);
        this.currentTransmitterTime = currentTransmitterTime;
        this.sessionStartTime = sessionStartTime;
        this.sessionStopTime = sessionStopTime;
        this.sessionStopReasonRaw = sessionStopReasonRaw;
        this.sessionDuration = sessionDuration;
    }

    public CgmStopSessionHistoryLog(long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int sessionStopReasonRaw, int sessionDuration) {
        this(0, 0, currentTransmitterTime, sessionStartTime, sessionStopTime, sessionStopReasonRaw, sessionDuration);
    }

    public int typeId() {
        return 214;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.currentTransmitterTime = Bytes.readUint32(raw, 10);
        this.sessionStartTime = Bytes.readUint32(raw, 14);
        this.sessionStopTime = Bytes.readUint32(raw, 18);
        this.sessionStopReasonRaw = raw[24] & 0xFF;
        this.sessionDuration = raw[25] & 0xFF;

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int sessionStopReasonRaw, int sessionDuration) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 214, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(currentTransmitterTime),
            Bytes.toUint32(sessionStartTime),
            Bytes.toUint32(sessionStopTime),
            new byte[]{0, 0},
            new byte[]{(byte) sessionStopReasonRaw},
            new byte[]{(byte) sessionDuration}));
    }

    /**
     * @return the current transmitter time, in seconds
     */
    public long getCurrentTransmitterTime() {
        return currentTransmitterTime;
    }

    /**
     * @return the session start time, in seconds
     */
    public long getSessionStartTime() {
        return sessionStartTime;
    }

    /**
     * @return the session stop time, in seconds
     */
    public long getSessionStopTime() {
        return sessionStopTime;
    }

    public int getSessionStopReasonRaw() {
        return sessionStopReasonRaw;
    }

    public enum SessionStopReason {
        DEXBLES_REASON_USER(0),
        DEXBLES_REASON_UNKNOWN(1),
        DEXBLES_REASON_TX_END_OF_LIFE(3),
        DEXBLES_REASON_TRANSMITTER_ERROR(4),
        DEXBLES_REASON_SESSION_STOP_SUCCESS(5),
        DEXBLES_REASON_TRANSMITTER_NOT_IN_SESSION(6),
        DEXBLES_REASON_NEW_SESSION_STARTED_SUCCESS(8),
        DEXBLES_REASON_SESSION_STARTED_IN_PROGRESS(9),
        DEXBLES_REASON_TRANSMITTER_IN_SESSION(10),
        DEXBLES_REASON_BLESTACK_INVALID(11),
        DEXBLES_REASON_NEW_AUTOCAL_SESSION_STARTED_SUCCESS(12),
        DEXBLES_REASON_NO_AUTOCAL_SESSION_IN_PROGRESS(13),

        ;
        private int id;
        SessionStopReason(int id) {
            this.id = id;
        }

        public static SessionStopReason fromId(int id) {
            for (SessionStopReason r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

    public SessionStopReason getSessionStopReason() {
        return SessionStopReason.fromId(sessionStopReasonRaw);
    }

    /**
     * @return the session duration, in days
     */
    public int getSessionDuration() {
        return sessionDuration;
    }
}
