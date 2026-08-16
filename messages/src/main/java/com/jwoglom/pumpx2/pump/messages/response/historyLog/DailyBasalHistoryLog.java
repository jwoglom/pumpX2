package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
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
    internalName = "LID_DAILY_BASAL",
    usedByTidepool = true
)
public class DailyBasalHistoryLog extends HistoryLog {
    
    private float dailyTotalBasal;
    private float lastBasalRate;
    private float iob;
    private boolean finalEventForDay;
    private int batteryChargeRaw;
    private int lipoMv;
    
    public DailyBasalHistoryLog() {}
    public DailyBasalHistoryLog(long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean finalEventForDay, int actualBatteryCharge, int lipoMv) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, dailyTotalBasal, lastBasalRate, iob, finalEventForDay, actualBatteryCharge, lipoMv);
        this.dailyTotalBasal = dailyTotalBasal;
        this.lastBasalRate = lastBasalRate;
        this.iob = iob;
        this.finalEventForDay = finalEventForDay;
        this.batteryChargeRaw = actualBatteryCharge;
        this.lipoMv = lipoMv;
        
    }

    public DailyBasalHistoryLog(float dailyTotalBasal, float lastBasalRate, float iob, boolean finalEventForDay, int actualBatteryCharge, int lipoMv) {
        this(0, 0, dailyTotalBasal, lastBasalRate, iob, finalEventForDay, actualBatteryCharge, lipoMv);
    }

    public int typeId() {
        return 81;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.dailyTotalBasal = Bytes.readFloat(raw, 10);
        this.lastBasalRate = Bytes.readFloat(raw, 14);
        this.iob = Bytes.readFloat(raw, 18);
        this.finalEventForDay = raw[22] == 1;
        this.batteryChargeRaw = raw[23] & 0xFF;
        this.lipoMv = Bytes.readShort(raw, 24);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float dailyTotalBasal, float lastBasalRate, float iob, boolean finalEventForDay, int actualBatteryCharge, int lipoMv) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(81, 0),
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
     * A close-out marker on the last {@link DailyBasalHistoryLog} of a reporting period. Its usual
     * trigger is the daily rollover just before midnight, after which {@link #getDailyTotalBasal()}
     * resets to 0, but it has also been observed immediately before a
     * {@link PumpingResumedHistoryLog} which ended an alarm-driven suspension, with no reset
     * following. TODO(confirm): the exact set of triggers is unconfirmed, so this should not be
     * relied upon as a day-boundary signal.
     *
     * @return whether this is the final event of the reporting period
     */
    public boolean getFinalEventForDay() {
        return finalEventForDay;
    }


    /**
     * @return the pump's reported battery state-of-charge, 0-100, unscaled
     */
    public int getBatteryChargeRaw() {
        return batteryChargeRaw;
    }

    /**
     * The pump reports its state of charge directly as a 0-100 percentage in this byte, so no
     * scaling is applied.
     *
     * @return the reported battery charge in percent
     */
    public double getBatteryChargePercent() {
       return batteryChargeRaw;
    }

    /**
     * @return the reported LIPO battery amount in millivolts
     */
    public int getLipoMv() {
        return lipoMv;
    }
}