package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * FreeStyle Libre 2 CGM session start.
 */
@HistoryLogProps(
    opCode = 404,
    displayName = "CGM Start Session FSL2",
    internalName = "LID_CGM_START_SESSION_FSL2"
)
public class CgmStartSessionFsl2HistoryLog extends HistoryLog {

    private long sessionStartTime;
    private int sessionDuration;

    public CgmStartSessionFsl2HistoryLog() {}
    public CgmStartSessionFsl2HistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0);
    }

    public CgmStartSessionFsl2HistoryLog(long pumpTimeSec, long sequenceNum, long sessionStartTime, int sessionDuration) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, sessionStartTime, sessionDuration);
        parse(cargo);
    }

    public int typeId() {
        return 404;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.sessionStartTime = Bytes.readUint32(raw, 10);
        this.sessionDuration = raw[14] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long sessionStartTime, int sessionDuration) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(404, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(sessionStartTime),
            new byte[]{(byte) sessionDuration}));
    }

    /**
     * @return session start time, in seconds since the Tandem epoch
     */
    public long getSessionStartTime() {
        return sessionStartTime;
    }
    /**
     * @return session duration, in days
     */
    public int getSessionDuration() {
        return sessionDuration;
    }
}
