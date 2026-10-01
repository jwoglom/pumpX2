package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * FreeStyle Libre 2 CGM session stop.
 */
@HistoryLogProps(
    opCode = 405,
    displayName = "CGM Stop Session FSL2",
    internalName = "LID_CGM_STOP_SESSION_FSL2"
)
public class CgmStopSessionFsl2HistoryLog extends HistoryLog {

    private long sessionStartTime;
    private long sessionStopTime;
    private int sessionDuration;
    private int sessionStopReason;

    public CgmStopSessionFsl2HistoryLog() {}
    public CgmStopSessionFsl2HistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0);
    }

    public CgmStopSessionFsl2HistoryLog(long pumpTimeSec, long sequenceNum, long sessionStartTime, long sessionStopTime, int sessionDuration, int sessionStopReason) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, sessionStartTime, sessionStopTime, sessionDuration, sessionStopReason);
        parse(cargo);
    }

    public int typeId() {
        return 405;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.sessionStartTime = Bytes.readUint32(raw, 10);
        this.sessionStopTime = Bytes.readUint32(raw, 14);
        this.sessionDuration = raw[18] & 0xFF;
        this.sessionStopReason = raw[19] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long sessionStartTime, long sessionStopTime, int sessionDuration, int sessionStopReason) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(405, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(sessionStartTime),
            Bytes.toUint32(sessionStopTime),
            new byte[]{(byte) sessionDuration},
            new byte[]{(byte) sessionStopReason}));
    }

    /**
     * @return session start time, in seconds since the Tandem epoch
     */
    public long getSessionStartTime() {
        return sessionStartTime;
    }
    /**
     * @return session stop time, in seconds since the Tandem epoch
     */
    public long getSessionStopTime() {
        return sessionStopTime;
    }
    /**
     * @return session duration, in days
     */
    public int getSessionDuration() {
        return sessionDuration;
    }
    /**
     * @return reason the session was stopped (values not documented by Tandem)
     */
    public int getSessionStopReason() {
        return sessionStopReason;
    }
}
