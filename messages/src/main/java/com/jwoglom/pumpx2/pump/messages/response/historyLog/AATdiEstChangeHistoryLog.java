package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 332,
    displayName = "AA TDI Estimate Change",
    internalName = "LID_AA_TDIEST_CHANGE"
)
public class AATdiEstChangeHistoryLog extends HistoryLog {


    public AATdiEstChangeHistoryLog() {}
    public AATdiEstChangeHistoryLog(long pumpTimeSec, long sequenceNum) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum);

    }

    public int typeId() {
        return 332;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{76, 1}, // typeId=332 (256+76)
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum)));
    }

}
