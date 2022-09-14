package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 2,
    displayName = "Temp Basal Rate Activated",
    usedByTidepool = true
)
public class TempRateActivatedHistoryLog extends HistoryLog {
    
    private float percent;
    private float duration;
    private int tempRateId;
    
    public TempRateActivatedHistoryLog() {}
    public TempRateActivatedHistoryLog(long pumpTimeSec, long sequenceNum, float percent, float duration, int tempRateId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, percent, duration, tempRateId);
        this.percent = percent;
        this.duration = duration;
        this.tempRateId = tempRateId;
        
    }

    public TempRateActivatedHistoryLog(float percent, float duration, int tempRateId) {
        this(0, 0, percent, duration, tempRateId);
    }

    public int typeId() {
        return 2;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.percent = Bytes.readFloat(raw, 10);
        this.duration = Bytes.readFloat(raw, 14);
        this.tempRateId = Bytes.readShort(raw, 20);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float percent, float duration, int tempRateId) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{2, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(percent), 
            Bytes.toFloat(duration), 
            Bytes.firstTwoBytesLittleEndian(tempRateId)));
    }
    public float getPercent() {
        return percent;
    }
    public float getDuration() {
        return duration;
    }
    public int getTempRateId() {
        return tempRateId;
    }
    
}