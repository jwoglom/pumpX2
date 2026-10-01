package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 212,
    displayName = "CGM Start Session GX",
    internalName = "LID_CGM_START_SESSION_GX"
)
public class CgmStartSessionHistoryLog extends HistoryLog {

    private long currentTransmitterTime;
    private long sessionStartTime;
    private int sessionDuration;
    private int sessionStartReason;

    public CgmStartSessionHistoryLog() {}
    public CgmStartSessionHistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0);
    }

    public CgmStartSessionHistoryLog(long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, int sessionDuration) {
        this(pumpTimeSec, sequenceNum, currentTransmitterTime, sessionStartTime, sessionDuration, 0);
    }

    public CgmStartSessionHistoryLog(long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, int sessionDuration, int sessionStartReason) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentTransmitterTime, sessionStartTime, sessionDuration, sessionStartReason);
        parse(cargo);
    }

    public CgmStartSessionHistoryLog(long currentTransmitterTime, long sessionStartTime, int sessionDuration) {
        this(0, 0, currentTransmitterTime, sessionStartTime, sessionDuration);
    }

    public int typeId() {
        return 212;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.currentTransmitterTime = Bytes.readUint32(raw, 10);
        this.sessionStartTime = Bytes.readUint32(raw, 14);
        // Tandem's cloud export stores bytes 22-25 as one byte-reversed word, so its offsets 15/14/13 are BLE bytes 22/23/24.
        this.sessionDuration = raw[22] & 0xFF;
        this.sessionStartReason = raw[23] & 0xFF;

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, int sessionDuration) {
        return buildCargo(pumpTimeSec, sequenceNum, currentTransmitterTime, sessionStartTime, sessionDuration, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, int sessionDuration, int sessionStartReason) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(212, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(currentTransmitterTime),
            Bytes.toUint32(sessionStartTime),
            new byte[]{0, 0, 0, 0},
            new byte[]{(byte) sessionDuration},
            new byte[]{(byte) sessionStartReason}));
    }

    /**
     * @return the current transmitter time in seconds
     */
    public long getCurrentTransmitterTime() {
        return currentTransmitterTime;
    }

    /**
     * @return the session start time in seconds
     */
    public long getSessionStartTime() {
        return sessionStartTime;
    }

    /**
     * @return the session duration in days
     */
    /**
     * @return session duration, in days
     */
    public int getSessionDuration() {
        return sessionDuration;
    }

    /**
     * @return raw session start reason; uses the same DEXBLES_REASON_* values as {@link CgmJoinSessionHistoryLog.SessionJoinReason}
     */
    public int getSessionStartReason() {
        return sessionStartReason;
    }
}
