package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 447,
    displayName = "CGM Stop Session (G7)",
    internalName = "LID_CGM_STOP_SESSION_G7"
)
public class CgmStopSessionG7HistoryLog extends HistoryLog {

    private long currentTransmitterTime;
    private long sessionStartTime;
    private long sessionStopTime;
    private int stopSessionCode;
    private int sessionStopReason;
    private int sessionDuration;

    public CgmStopSessionG7HistoryLog() {}
    public CgmStopSessionG7HistoryLog(long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int stopSessionCode, int sessionStopReason, int sessionDuration) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentTransmitterTime, sessionStartTime, sessionStopTime, stopSessionCode, sessionStopReason, sessionDuration);
        this.currentTransmitterTime = currentTransmitterTime;
        this.sessionStartTime = sessionStartTime;
        this.sessionStopTime = sessionStopTime;
        this.stopSessionCode = stopSessionCode;
        this.sessionStopReason = sessionStopReason;
        this.sessionDuration = sessionDuration;

    }

    public CgmStopSessionG7HistoryLog(long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int stopSessionCode, int sessionStopReason, int sessionDuration) {
        this(0, 0, currentTransmitterTime, sessionStartTime, sessionStopTime, stopSessionCode, sessionStopReason, sessionDuration);
    }

    public int typeId() {
        return 447;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.currentTransmitterTime = Bytes.readUint32(raw, 10);
        this.sessionStartTime = Bytes.readUint32(raw, 14);
        this.sessionStopTime = Bytes.readUint32(raw, 18);
        this.stopSessionCode = raw[23];
        this.sessionStopReason = raw[24];
        this.sessionDuration = raw[25];

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int stopSessionCode, int sessionStopReason, int sessionDuration) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(447, 0), // 447 = 0x01BF
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(currentTransmitterTime),
            Bytes.toUint32(sessionStartTime),
            Bytes.toUint32(sessionStopTime),
            new byte[]{0}, // unused padding byte at offset 22
            new byte[]{ (byte) stopSessionCode },
            new byte[]{ (byte) sessionStopReason },
            new byte[]{ (byte) sessionDuration }));
    }

    public long getCurrentTransmitterTime() {
        return currentTransmitterTime;
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public long getSessionStopTime() {
        return sessionStopTime;
    }

    public int getStopSessionCode() {
        return stopSessionCode;
    }

    public int getSessionStopReason() {
        return sessionStopReason;
    }

    public int getSessionDuration() {
        return sessionDuration;
    }
}
