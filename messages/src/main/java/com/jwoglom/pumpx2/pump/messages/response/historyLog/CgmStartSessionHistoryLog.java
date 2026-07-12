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

    public CgmStartSessionHistoryLog() {}
    public CgmStartSessionHistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0);
    }

    public CgmStartSessionHistoryLog(long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, int sessionDuration) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentTransmitterTime, sessionStartTime, sessionDuration);
        this.currentTransmitterTime = currentTransmitterTime;
        this.sessionStartTime = sessionStartTime;
        this.sessionDuration = sessionDuration;
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
        this.sessionDuration = raw[25] & 0xFF;

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, int sessionDuration) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 212, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(currentTransmitterTime),
            Bytes.toUint32(sessionStartTime),
            new byte[]{0, 0, 0, 0, 0, 0, 0},
            new byte[]{(byte) sessionDuration}));
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
    public int getSessionDuration() {
        return sessionDuration;
    }
}
