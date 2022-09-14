package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 81,
    displayName = "Daily Basal",
    usedByTidepool = true
)
public class DailyBasalHistoryLog extends HistoryLog {
    
    private float dailyTotalBasal;
    private float lastBasalRate;
    private float iob;
    private int actualBatteryCharge;
    private int lipoMv;
    
    public DailyBasalHistoryLog() {}
    public DailyBasalHistoryLog(long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, int actualBatteryCharge, int lipoMv) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, dailyTotalBasal, lastBasalRate, iob, actualBatteryCharge, lipoMv);
        this.dailyTotalBasal = dailyTotalBasal;
        this.lastBasalRate = lastBasalRate;
        this.iob = iob;
        this.actualBatteryCharge = actualBatteryCharge;
        this.lipoMv = lipoMv;
        
    }

    public DailyBasalHistoryLog(float dailyTotalBasal, float lastBasalRate, float iob, int actualBatteryCharge, int lipoMv) {
        this(0, 0, dailyTotalBasal, lastBasalRate, iob, actualBatteryCharge, lipoMv);
    }

    public int typeId() {
        return 81;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.dailyTotalBasal = Bytes.readFloat(raw, 10);
        this.lastBasalRate = Bytes.readFloat(raw, 14);
        this.iob = Bytes.readFloat(raw, 18);
        this.actualBatteryCharge = raw[23];
        this.lipoMv = Bytes.readShort(raw, 24);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, int actualBatteryCharge, int lipoMv) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{81, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(dailyTotalBasal), 
            Bytes.toFloat(lastBasalRate), 
            Bytes.toFloat(iob), 
            new byte[]{ (byte) actualBatteryCharge }, 
            Bytes.firstTwoBytesLittleEndian(lipoMv)));
    }
    public float getDailyTotalBasal() {
        return dailyTotalBasal;
    }
    public float getLastBasalRate() {
        return lastBasalRate;
    }
    public float getIob() {
        return iob;
    }
    public int getActualBatteryCharge() {
        return actualBatteryCharge;
    }
    public int getLipoMv() {
        return lipoMv;
    }
    
}