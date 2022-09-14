package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 0,
    displayName = "Log Erased",
    usedByTidepool = true
)
public class LogErasedHistoryLog extends HistoryLog {
    
    private long numErased;
    
    public LogErasedHistoryLog() {}
    public LogErasedHistoryLog(long pumpTimeSec, long sequenceNum, long numErased) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, numErased);
        this.numErased = numErased;
        
    }

    public LogErasedHistoryLog(long numErased) {
        this(0, 0, numErased);
    }

    public int typeId() {
        return 0;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.numErased = Bytes.readUint32(raw, 10);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long numErased) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{0, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(numErased)));
    }
    public long getNumErased() {
        return numErased;
    }
    
}