package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 394,
    displayName = "CGM Join Session G7",
    internalName = "LID_CGM_JOIN_SESSION_G7"
)
public class CgmJoinSessionG7HistoryLog extends HistoryLog {

    private long cgmTimestamp;
    private long sessionSignature;

    public CgmJoinSessionG7HistoryLog() {}
    public CgmJoinSessionG7HistoryLog(long pumpTimeSec, long sequenceNum, long cgmTimestamp, long sessionSignature) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, cgmTimestamp, sessionSignature);
        this.cgmTimestamp = cgmTimestamp;
        this.sessionSignature = sessionSignature;

    }

    public CgmJoinSessionG7HistoryLog(long cgmTimestamp, long sessionSignature) {
        this(0, 0, cgmTimestamp, sessionSignature);
    }

    public int typeId() {
        return 394;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.cgmTimestamp = Bytes.readUint32(raw, 10);
        this.sessionSignature = Bytes.readUint32(raw, 14);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long cgmTimestamp, long sessionSignature) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 394, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(cgmTimestamp),
            Bytes.toUint32(sessionSignature)));
    }

    public long getCgmTimestamp() {
        return cgmTimestamp;
    }

    public long getSessionSignature() {
        return sessionSignature;
    }
}
