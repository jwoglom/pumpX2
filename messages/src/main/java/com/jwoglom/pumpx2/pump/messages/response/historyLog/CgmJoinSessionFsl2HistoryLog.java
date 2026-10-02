package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * FreeStyle Libre 2 CGM session join.
 */
@HistoryLogProps(
    opCode = 406,
    displayName = "CGM Join Session FSL2",
    internalName = "LID_CGM_JOIN_SESSION_FSL2"
)
public class CgmJoinSessionFsl2HistoryLog extends HistoryLog {

    private long sessionStartTime;
    private long sessionJoinTime;
    private int sessionDuration;
    private int sessionJoinReason;

    public CgmJoinSessionFsl2HistoryLog() {}
    public CgmJoinSessionFsl2HistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0);
    }

    public CgmJoinSessionFsl2HistoryLog(long pumpTimeSec, long sequenceNum, long sessionStartTime, long sessionJoinTime, int sessionDuration, int sessionJoinReason) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, sessionStartTime, sessionJoinTime, sessionDuration, sessionJoinReason);
        parse(cargo);
    }

    public int typeId() {
        return 406;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.sessionStartTime = Bytes.readUint32(raw, 10);
        this.sessionJoinTime = Bytes.readUint32(raw, 14);
        this.sessionDuration = raw[18] & 0xFF;
        this.sessionJoinReason = raw[19] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long sessionStartTime, long sessionJoinTime, int sessionDuration, int sessionJoinReason) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(406, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(sessionStartTime),
            Bytes.toUint32(sessionJoinTime),
            new byte[]{(byte) sessionDuration},
            new byte[]{(byte) sessionJoinReason}));
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
}
