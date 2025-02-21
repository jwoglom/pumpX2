package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.helpers.Dates;

import java.time.Instant;

@HistoryLogProps(
    opCode = 14,
    displayName = "Date Change",
    usedByAndroid = true,
    usedByTidepool = true
)
public class DateChangeHistoryLog extends HistoryLog {
    
    private long datePrior;
    private long dateAfter;
    private long rawRTCTime;
    
    public DateChangeHistoryLog() {}
    
    public DateChangeHistoryLog(long pumpTimeSec, long sequenceNum, long datePrior, long dateAfter, long rawRTCTime) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, datePrior, dateAfter, rawRTCTime);
        this.pumpTimeSec = pumpTimeSec;
        this.sequenceNum = sequenceNum;
        this.datePrior = datePrior;
        this.dateAfter = dateAfter;
        this.rawRTCTime = rawRTCTime;
        
    }

    public int typeId() {
        return 14;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.datePrior = Bytes.readUint32(raw, 10);
        this.dateAfter = Bytes.readUint32(raw, 14);
        this.rawRTCTime = Bytes.readUint32(raw, 18);
        
    }

    
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long datePrior, long dateAfter, long rawRTCTime) {
        return Bytes.combine(
            new byte[]{14, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(datePrior), 
            Bytes.toUint32(dateAfter), 
            Bytes.toUint32(rawRTCTime),
            new byte[4]);
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

    public Instant getDatePriorInstant() {
        return Dates.fromJan12008EpochDaysToDate(datePrior);
    }

    public Instant getDateAfterInstant() {
        return Dates.fromJan12008EpochDaysToDate(dateAfter);
    }
}