package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 214,
    displayName = "CGM Stop Session",
    internalName = "LID_CGM_STOP_SESSION_GX"
)
public class CgmStopSessionHistoryLog extends HistoryLog {

    public CgmStopSessionHistoryLog() {}
    public CgmStopSessionHistoryLog(long pumpTimeSec, long sequenceNum) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum);

    }

    public int typeId() {
        return 214;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 214, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum)));
    }
}
