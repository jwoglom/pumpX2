package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Control-IQ's state for one 5-minute algorithm cycle: its glucose estimate and prediction,
 * net IOB, TDI estimate, correction factor, target, the programmed basal it starts from, and
 * what it commands (the hypoglycemia brake, the cycle's insulin and the increase above profile).
 * Written every ~300 s on t:slim X2 and Mobi, including with Control-IQ off and with no CGM,
 * directly before opcode 327 in the same second and before the next {@link BasalDeliveryHistoryLog}
 * (about 30 s later on Mobi, about 90 s on the X2).
 *
 * <p>Evidence, from about 11,000 records in 10 pump datasets: {@link #getProgrammedBasalRate()} matches
 * the next BasalDelivery's programmed rate in 99.5%; {@link #getHypoBrake()} is the stated function
 * of {@link #getPredictedGlucoseX100()} in 96-100% per dataset; with Control-IQ on,
 * {@link #getCycleInsulinMilliunits()} is the next BasalDelivery's algorithm rate over 12 (plus any
 * automatic correction bolus) in 98-99%. See https://github.com/jwoglom/pumpX2/issues/146.
 *
 * <p>These are pump-internal Control-IQ values. Do not use them for dosing or as the IOB.
 *
 * <p>The class name is provisional: opcode 227 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. {@link #getSleepRamp()} is exposed raw because its meaning is unknown.
 */
@HistoryLogProps(
    opCode = 227,
    displayName = "Control-IQ Cycle"
)
public class ControlIQCycleHistoryLog extends HistoryLog {

    private int basalIncreaseMilliunits;
    private int cycleInsulinMilliunits;
    private int predictedGlucoseX100;
    private int programmedBasalRate;
    private int netIobMilliunits;
    private int glucoseEstimate;
    private int correctionFactor;
    private int tdiEstimate;
    private int hypoBrake;
    private int sleepRamp;
    private int upperTarget;

    public ControlIQCycleHistoryLog() {}
    public ControlIQCycleHistoryLog(long pumpTimeSec, long sequenceNum, int basalIncreaseMilliunits, int cycleInsulinMilliunits, int predictedGlucoseX100, int programmedBasalRate, int netIobMilliunits, int glucoseEstimate, int correctionFactor, int tdiEstimate, int hypoBrake, int sleepRamp, int upperTarget) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, basalIncreaseMilliunits, cycleInsulinMilliunits, predictedGlucoseX100, programmedBasalRate, netIobMilliunits, glucoseEstimate, correctionFactor, tdiEstimate, hypoBrake, sleepRamp, upperTarget);
        this.basalIncreaseMilliunits = basalIncreaseMilliunits;
        this.cycleInsulinMilliunits = cycleInsulinMilliunits;
        this.predictedGlucoseX100 = predictedGlucoseX100;
        this.programmedBasalRate = programmedBasalRate;
        this.netIobMilliunits = netIobMilliunits;
        this.glucoseEstimate = glucoseEstimate;
        this.correctionFactor = correctionFactor;
        this.tdiEstimate = tdiEstimate;
        this.hypoBrake = hypoBrake;
        this.sleepRamp = sleepRamp;
        this.upperTarget = upperTarget;
    }

    public int typeId() {
        return 227;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.basalIncreaseMilliunits = Bytes.readShort(raw, 10);
        this.cycleInsulinMilliunits = Bytes.readShort(raw, 12);
        this.predictedGlucoseX100 = Bytes.readShort(raw, 14);
        this.programmedBasalRate = Bytes.readShort(raw, 16);
        this.netIobMilliunits = Bytes.readShort(raw, 18);
        this.glucoseEstimate = raw[20] & 0xFF;
        this.correctionFactor = raw[21] & 0xFF;
        this.tdiEstimate = raw[22] & 0xFF;
        this.hypoBrake = raw[23] & 0xFF;
        this.sleepRamp = raw[24] & 0xFF;
        this.upperTarget = raw[25] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int basalIncreaseMilliunits, int cycleInsulinMilliunits, int predictedGlucoseX100, int programmedBasalRate, int netIobMilliunits, int glucoseEstimate, int correctionFactor, int tdiEstimate, int hypoBrake, int sleepRamp, int upperTarget) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(227, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(basalIncreaseMilliunits),
            Bytes.firstTwoBytesLittleEndian(cycleInsulinMilliunits),
            Bytes.firstTwoBytesLittleEndian(predictedGlucoseX100),
            Bytes.firstTwoBytesLittleEndian(programmedBasalRate),
            Bytes.firstTwoBytesLittleEndian(netIobMilliunits),
            Bytes.firstByteLittleEndian(glucoseEstimate),
            Bytes.firstByteLittleEndian(correctionFactor),
            Bytes.firstByteLittleEndian(tdiEstimate),
            Bytes.firstByteLittleEndian(hypoBrake),
            Bytes.firstByteLittleEndian(sleepRamp),
            Bytes.firstByteLittleEndian(upperTarget)));
    }

    /**
     * floor(max(0, algorithmRate - profileBasalRate) / 12) for the next
     * {@link BasalDeliveryHistoryLog}. Nonzero only when Control-IQ raises basal above the profile
     * (never with Control-IQ off).
     *
     * @return the cycle's basal increase above the profile rate, in milliunits per 5 minutes
     */
    public int getBasalIncreaseMilliunits() {
        return basalIncreaseMilliunits;
    }

    /**
     * With Control-IQ on: floor(algorithmRate / 12) for the next {@link BasalDeliveryHistoryLog},
     * plus the requested amount of a Control-IQ automatic correction bolus started in this cycle.
     * With Control-IQ off: close to floor(programmedBasalRate / 12 * hypoBrakeFraction) (81-99% of
     * records on three Mobis), which the pump then does not deliver.
     *
     * @return the insulin the algorithm computes for this cycle, in milliunits
     */
    public int getCycleInsulinMilliunits() {
        return cycleInsulinMilliunits;
    }

    /**
     * Predicted glucose x100, floored at 2000. Roughly the glucose estimate plus 20 minutes of the
     * recent CGM trend, minus an insulin effect; the exact prediction horizon is not confirmed.
     *
     * @return the predicted glucose in mg/dL x 100
     */
    public int getPredictedGlucoseX100() {
        return predictedGlucoseX100;
    }

    /**
     * @return {@link #getPredictedGlucoseX100()} in mg/dL
     */
    public double getPredictedGlucoseMgdl() {
        return predictedGlucoseX100 / 100.0;
    }

    /**
     * The active temp rate if one is running, else the profile rate; the profile rate while
     * suspended. Capped at 3000. On Control-IQ v7.6 Mobi firmware this is the profile rate even
     * during a temp rate.
     *
     * @return the programmed basal rate, in milliunits/hr
     */
    public int getProgrammedBasalRate() {
        return programmedBasalRate;
    }

    /**
     * Not the IOB the pump displays. Fits boluses plus (delivered - programmed) basal through a
     * two-compartment curve (1 + t/tau) * e^(-t/tau) with tau = 34 minutes, floored at 0
     * (r 0.97-0.9998 in 8 datasets).
     *
     * @return the algorithm's net insulin on board, in milliunits
     */
    public int getNetIobMilliunits() {
        return netIobMilliunits;
    }

    /**
     * Tracks the latest CGM reading (r 0.997-0.998, usually 1 mg/dL below it) and saturates at 255.
     * With no CGM paired to the pump it stays near a fixed value.
     *
     * @return the algorithm's current glucose estimate, in mg/dL
     */
    public int getGlucoseEstimate() {
        return glucoseEstimate;
    }

    /**
     * min(floor(1800 / TDI estimate), profile correction factor) in 2024-2026 data; 1500 instead of
     * 1800 in the 2022 records and part of 2023.
     *
     * @return the correction factor the algorithm uses, in mg/dL per unit
     */
    public int getCorrectionFactor() {
        return correctionFactor;
    }

    /**
     * The integer part of the estimate in the AATdiEstChange record (opcode 332) that the Mobi writes
     * hourly in the same second, just before this record.
     *
     * @return the total daily insulin estimate, in whole units per day
     */
    public int getTdiEstimate() {
        return tdiEstimate;
    }

    /**
     * floor(200 / (1 + 2.5 * r)) for predicted glucose G = {@link #getPredictedGlucoseMgdl()} &gt;= 70,
     * and 0 below 70, where r = 10 * f^2 when f = 1.509 * (ln(G)^1.084 - 5.381) is negative and 0
     * otherwise (Kovatchev's low-BG risk, zero at 112.5 mg/dL). 200 means no attenuation.
     *
     * @return the hypoglycemia brake factor, 0-200
     */
    public int getHypoBrake() {
        return hypoBrake;
    }

    /**
     * @return {@link #getHypoBrake()} as a fraction, 0.0 (full brake) to 1.0 (none)
     */
    public double getHypoBrakeFraction() {
        return hypoBrake / 200.0;
    }

    /**
     * 0 outside sleep. In sleep mode it rises by 3 or 4 per cycle (40 per hour) to 200 over five
     * hours; after sleep ends it falls by 4-5 per cycle. Meaning unknown.
     *
     * @return raw uint8 at offset 24
     */
    public int getSleepRamp() {
        return sleepRamp;
    }

    /**
     * 160 in normal and exercise mode and 120 in sleep mode, moving between them over about
     * 40-90 minutes after a mode change.
     *
     * @return the upper bound of the Control-IQ target range, in mg/dL
     */
    public int getUpperTarget() {
        return upperTarget;
    }
}
