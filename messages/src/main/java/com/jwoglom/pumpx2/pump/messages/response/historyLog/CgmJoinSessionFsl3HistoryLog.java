package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * FreeStyle Libre 3 CGM session join.
 */
@HistoryLogProps(
    opCode = 477,
    displayName = "CGM Join Session (FSL3)",
    internalName = "LID_CGM_JOIN_SESSION_FSL3"
)
public class CgmJoinSessionFsl3HistoryLog extends HistoryLog {

    private long sessionStartTime;
    private long sessionJoinTime;
    private int sessionDuration;
    private int sessionJoinReason;
    private long sessionDurationSecs;

    public CgmJoinSessionFsl3HistoryLog() {}
    public CgmJoinSessionFsl3HistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0);
    }

    public CgmJoinSessionFsl3HistoryLog(long pumpTimeSec, long sequenceNum, long sessionStartTime, long sessionJoinTime, int sessionDuration, int sessionJoinReason, long sessionDurationSecs) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, sessionStartTime, sessionJoinTime, sessionDuration, sessionJoinReason, sessionDurationSecs);
        parse(cargo);
    }

    public int typeId() {
        return 477;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.sessionStartTime = Bytes.readUint32(raw, 10);
        this.sessionJoinTime = Bytes.readUint32(raw, 14);
        this.sessionDuration = raw[18] & 0xFF;
        this.sessionJoinReason = raw[19] & 0xFF;
        this.sessionDurationSecs = Bytes.readUint32(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long sessionStartTime, long sessionJoinTime, int sessionDuration, int sessionJoinReason, long sessionDurationSecs) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(477, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(sessionStartTime),
            Bytes.toUint32(sessionJoinTime),
            new byte[]{(byte) sessionDuration},
            new byte[]{(byte) sessionJoinReason},
            new byte[]{0, 0},
            Bytes.toUint32(sessionDurationSecs)));
    }

    /**
     * @return session start time, in seconds since the Tandem epoch
     */
    public long getSessionStartTime() {
        return sessionStartTime;
    }
    /**
     * @return session join time, in seconds since the Tandem epoch
     */
    public long getSessionJoinTime() {
        return sessionJoinTime;
    }
    /**
     * @return session duration, in days
     */
    public int getSessionDuration() {
        return sessionDuration;
    }
    /**
     * @return reason the session was joined (values not documented by Tandem)
     */
    public int getSessionJoinReason() {
        return sessionJoinReason;
    }
    /**
     * @return session duration, in seconds
     */
    public long getSessionDurationSecs() {
        return sessionDurationSecs;
    }
}
