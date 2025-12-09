package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 140,
    displayName = "PLGS Periodic",
    internalName = "LID_PLGS_PERIODIC"
)
public class PlgsPeriodicHistoryLog extends HistoryLog {

    public PlgsPeriodicHistoryLog() {}
    public PlgsPeriodicHistoryLog(long pumpTimeSec, long sequenceNum) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum);

    }

    public int typeId() {
        return 140;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 140, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum)));
    }
}
