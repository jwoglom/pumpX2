package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Control-IQ's rolling two-hour total of the insulin it commanded itself, written every 15 minutes
 * (a 900-second timer from pump start-up, in the same second of the minute as
 * {@link DailyBasalHistoryLog}). The three floats are sums in units over the last 24 five-minute
 * Control-IQ cycles: for every cycle in which the algorithm was in control ({@link
 * BasalDeliveryHistoryLog} source ALGORITHM or TEMP_RATE_AND_ALGORITHM), the commanded basal rate
 * times 5 minutes, plus each automatic correction bolus. User-initiated boluses are not counted.
 * Rebuilt from the 227 cycle times, BasalDelivery (279) rates and automatic BolusDelivery (280)
 * records, all three fields matched within 0.005 U in 84-100% of 1,241 records from four pumps
 * (t:slim X2 and Mobi); the misses are one five-minute slot off, for exactly two hours.
 *
 * <p>All three are exactly 0 until Control-IQ has run in closed loop since the pump last started
 * ({@link ArmInitHistoryLog}). When closed loop stops, old cycles still expire, so the sums fall to
 * float rounding residue (magnitude below 2e-5 U), not to exactly 0.
 *
 * <p>These are pump-internal Control-IQ values: do not use them for dosing or IOB.
 *
 * <p>The class name is provisional: opcode 237 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. {@link #getUnknown22()} is exposed raw. See
 * https://github.com/jwoglom/pumpX2/issues/146.
 */
@HistoryLogProps(
    opCode = 237,
    displayName = "Control-IQ Automated Insulin"
)
public class ControlIQAutomatedInsulinHistoryLog extends HistoryLog {
    /** Larger than the float residue the sums settle at, smaller than one 5-minute basal slot. */
    public static final float RESIDUE_THRESHOLD_UNITS = 0.001f;

    private float totalInsulin;
    private float basalAboveProfileInsulin;
    private float autoBolusInsulin;
    private long unknown22;

    public ControlIQAutomatedInsulinHistoryLog() {}
    public ControlIQAutomatedInsulinHistoryLog(long pumpTimeSec, long sequenceNum, float totalInsulin, float basalAboveProfileInsulin, float autoBolusInsulin, long unknown22) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, totalInsulin, basalAboveProfileInsulin, autoBolusInsulin, unknown22);
        this.totalInsulin = totalInsulin;
        this.basalAboveProfileInsulin = basalAboveProfileInsulin;
        this.autoBolusInsulin = autoBolusInsulin;
        this.unknown22 = unknown22;
    }

    public int typeId() {
        return 237;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.totalInsulin = Bytes.readFloat(raw, 10);
        this.basalAboveProfileInsulin = Bytes.readFloat(raw, 14);
        this.autoBolusInsulin = Bytes.readFloat(raw, 18);
        this.unknown22 = Bytes.readUint32(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float totalInsulin, float basalAboveProfileInsulin, float autoBolusInsulin, long unknown22) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(237, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(totalInsulin),
            Bytes.toFloat(basalAboveProfileInsulin),
            Bytes.toFloat(autoBolusInsulin),
            Bytes.toUint32(unknown22)));
    }

    /**
     * Basal Control-IQ commanded over the last two hours plus its automatic correction boluses.
     * Always at least {@link #getBasalAboveProfileInsulin()} + {@link #getAutoBolusInsulin()}.
     *
     * @return insulin in units (float32 at offset 10)
     */
    public float getTotalInsulin() {
        return totalInsulin;
    }

    /**
     * For each cycle in the window, max(0, commanded rate - profile rate) times 5 minutes: the
     * insulin Control-IQ added by raising basal above the profile.
     *
     * @return insulin in units (float32 at offset 14)
     */
    public float getBasalAboveProfileInsulin() {
        return basalAboveProfileInsulin;
    }

    /**
     * Automatic correction boluses in the window, counted at the Control-IQ cycle that decided them
     * (47-141 s before the BolusDelivery STARTED record) and dropped two hours later. Matched
     * the requested amount of all 20 automatic boluses seen, to 0.001 U. Three times it also held
     * an amount with no automatic bolus record; each time the Control-IQ cycle fell during a user
     * bolus.
     *
     * @return insulin in units (float32 at offset 18)
     */
    public float getAutoBolusInsulin() {
        return autoBolusInsulin;
    }

    /**
     * @return {@link #getTotalInsulin()} minus {@link #getAutoBolusInsulin()}: the basal Control-IQ
     * commanded over the last two hours, in units
     */
    public float getBasalInsulin() {
        return totalInsulin - autoBolusInsulin;
    }

    /**
     * @return false when every sum is 0 or float residue, i.e. Control-IQ commanded no insulin in
     * the last two hours (or has not run in closed loop since the pump started)
     */
    public boolean hasAutomatedInsulin() {
        return Math.abs(totalInsulin) >= RESIDUE_THRESHOLD_UNITS
            || Math.abs(basalAboveProfileInsulin) >= RESIDUE_THRESHOLD_UNITS
            || Math.abs(autoBolusInsulin) >= RESIDUE_THRESHOLD_UNITS;
    }

    /**
     * 0 in all 4,668 records seen.
     *
     * @return raw uint32 at offset 22
     */
    public long getUnknown22() {
        return unknown22;
    }
}
