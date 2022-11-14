package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * A message which appears in the history log every hour at minimum, more frequently when the basal
 * rate changes, and contains a summary of the total basal deliveries up to that point in the current
 * day. Resets at 00:00 midnight when there is a {@link NewDayHistoryLog}.
 */
@HistoryLogProps(
    opCode = 81,
    displayName = "Daily Basal",
    usedByTidepool = true
)
public class DailyBasalHistoryLog extends HistoryLog {
    
    private float dailyTotalBasal;
    private float lastBasalRate;
    private float iob;
    private boolean finalEventForDay;
    private int actualBatteryCharge;
    private int lipoMv;
    
    public DailyBasalHistoryLog() {}
    public DailyBasalHistoryLog(long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean finalEventForDay, int actualBatteryCharge, int lipoMv) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, dailyTotalBasal, lastBasalRate, iob, finalEventForDay, actualBatteryCharge, lipoMv);
        this.dailyTotalBasal = dailyTotalBasal;
        this.lastBasalRate = lastBasalRate;
        this.iob = iob;
        this.finalEventForDay = finalEventForDay;
        this.actualBatteryCharge = actualBatteryCharge;
        this.lipoMv = lipoMv;
        
    }

    public DailyBasalHistoryLog(float dailyTotalBasal, float lastBasalRate, float iob, boolean finalEventForDay, int actualBatteryCharge, int lipoMv) {
        this(0, 0, dailyTotalBasal, lastBasalRate, iob, finalEventForDay, actualBatteryCharge, lipoMv);
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
        this.finalEventForDay = raw[22] == 1;
        this.actualBatteryCharge = raw[23];
        this.lipoMv = Bytes.readShort(raw, 24);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean finalEventForDay, int actualBatteryCharge, int lipoMv) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{81, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(dailyTotalBasal), 
            Bytes.toFloat(lastBasalRate), 
            Bytes.toFloat(iob),
            new byte[]{ (byte) (finalEventForDay ? 1 : 0) },
            new byte[]{ (byte) actualBatteryCharge },
            Bytes.firstTwoBytesLittleEndian(lipoMv)));
    }

    /**
     * @return the total basal amount delivered in the gap between this message and the previous
     */
    public float getDailyTotalBasal() {
        return dailyTotalBasal;
    }

    /**
     * @return the last basal rate used as of this message
     */
    public float getLastBasalRate() {
        return lastBasalRate;
    }

    /**
     * @return the current IOB
     */
    public float getIob() {
        return iob;
    }

    /**
     * @return TODO(confirm): whether this is the final event of the day
     */
    public boolean getFinalEventForDay() {
        return finalEventForDay;
    }

    /**
     * @return the reported battery charge in percent
     */
    public int getActualBatteryCharge() {
        return actualBatteryCharge;
    }

    /**
     * @return the reported LIPO battery amount in millivolts
     */
    public int getLipoMv() {
        return lipoMv;
    }
}