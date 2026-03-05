package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 235,
    displayName = "AA Sleep Schedule Change",
    internalName = "LID_AA_SLEEP_SCHEDULE_CHANGE",
    usedByAndroid = true
)
public class AaSleepScheduleChangeHistoryLog extends HistoryLog {


    public AaSleepScheduleChangeHistoryLog() {}
    public AaSleepScheduleChangeHistoryLog(long pumpTimeSec, long sequenceNum) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum);

    }

    public int typeId() {
        return 235;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte)(235 & 0xFF), (byte)(235 >> 8)}, // 235
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum)));
    }

}
