package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

public class DateChangeHistoryLog extends HistoryLog {
    
    private long datePrior;
    private long dateAfter;
    private long rawRTCTime;
    
    public DateChangeHistoryLog() {}
    
    public DateChangeHistoryLog(long datePrior, long dateAfter, long rawRTCTime) {
        this.cargo = buildCargo(datePrior, dateAfter, rawRTCTime);
        this.datePrior = datePrior;
        this.dateAfter = dateAfter;
        this.rawRTCTime = rawRTCTime;
        
    }

    public int typeId() {
        return 14;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        this.datePrior = Bytes.readUint32(raw, 10);
        this.dateAfter = Bytes.readUint32(raw, 14);
        this.rawRTCTime = Bytes.readUint32(raw, 18);
        
    }

    
    public static byte[] buildCargo(long datePrior, long dateAfter, long rawRTCTime) {
        return Bytes.combine(
            Bytes.toUint32(datePrior), 
            Bytes.toUint32(dateAfter), 
            Bytes.toUint32(rawRTCTime));
    }
    
    public long getDatePrior() {
        return datePrior;
    }
    public long getDateAfter() {
        return dateAfter;
    }
    public long getRawRTCTime() {
        return rawRTCTime;
    }
    
}