package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 15,
    displayName = "Temporary Basal Rate Completed",
    usedByTidepool = true
)
public class TempRateCompletedHistoryLog extends HistoryLog {
    
    private int tempRateId;
    private long timeLeft;
    
    public TempRateCompletedHistoryLog() {}
    public TempRateCompletedHistoryLog(long pumpTimeSec, long sequenceNum, int tempRateId, long timeLeft) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, tempRateId, timeLeft);
        this.tempRateId = tempRateId;
        this.timeLeft = timeLeft;
        
    }

    public TempRateCompletedHistoryLog(int tempRateId, long timeLeft) {
        this(0, 0, tempRateId, timeLeft);
    }

    public int typeId() {
        return 15;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.tempRateId = Bytes.readShort(raw, 12);
        this.timeLeft = Bytes.readUint32(raw, 14);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int tempRateId, long timeLeft) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{15, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(tempRateId), 
            Bytes.toUint32(timeLeft)));
    }
    public int getTempRateId() {
        return tempRateId;
    }
    public long getTimeLeft() {
        return timeLeft;
    }
    
}