package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 13,
    displayName = "Time Change",
    usedByTidepool = true
)
public class TimeChangedHistoryLog extends HistoryLog {
    
    private long timePrior;
    private long timeAfter;
    private long rawRTC;
    
    public TimeChangedHistoryLog() {}
    public TimeChangedHistoryLog(long pumpTimeSec, long sequenceNum, long timePrior, long timeAfter, long rawRTC) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, timePrior, timeAfter, rawRTC);
        this.timePrior = timePrior;
        this.timeAfter = timeAfter;
        this.rawRTC = rawRTC;
        
    }

    public TimeChangedHistoryLog(long timePrior, long timeAfter, long rawRTC) {
        this(0, 0, timePrior, timeAfter, rawRTC);
    }

    public int typeId() {
        return 13;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.timePrior = Bytes.readUint32(raw, 10);
        this.timeAfter = Bytes.readUint32(raw, 14);
        this.rawRTC = Bytes.readUint32(raw, 18);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long timePrior, long timeAfter, long rawRTC) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{13, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(timePrior), 
            Bytes.toUint32(timeAfter), 
            Bytes.toUint32(rawRTC)));
    }
    public long getTimePrior() {
        return timePrior;
    }
    public long getTimeAfter() {
        return timeAfter;
    }
    public long getRawRTC() {
        return rawRTC;
    }
    
}