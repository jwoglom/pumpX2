package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 60,
    displayName = "Data Log Corruption",
    usedByTidepool = true
)
public class DataLogCorruptionHistoryLog extends HistoryLog {
    
    
    public DataLogCorruptionHistoryLog() {}
    public DataLogCorruptionHistoryLog(long pumpTimeSec, long sequenceNum) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum);
        
    }

    public int typeId() {
        return 60;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{60, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum)));
    }
    
}