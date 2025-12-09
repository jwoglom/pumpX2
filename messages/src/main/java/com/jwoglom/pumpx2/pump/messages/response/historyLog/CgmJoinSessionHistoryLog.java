package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 213,
    displayName = "CGM Join Session",
    internalName = "LID_CGM_JOIN_SESSION_GX"
)
public class CgmJoinSessionHistoryLog extends HistoryLog {

    public CgmJoinSessionHistoryLog() {}
    public CgmJoinSessionHistoryLog(long pumpTimeSec, long sequenceNum) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum);

    }

    public int typeId() {
        return 213;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 213, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum)));
    }
}
