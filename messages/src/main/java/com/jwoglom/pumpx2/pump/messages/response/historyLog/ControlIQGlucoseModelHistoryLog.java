package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Five-minute Control-IQ model record, written in the same pump second as opcode 227
 * ({@code ControlIQCycleHistoryLog}) and always at that record's sequence number + 1 (10,025 of
 * 10,025 pairs, ten t:slim X2 and Mobi pumps). It holds the algorithm's filtered glucose, a
 * short-horizon glucose prediction, its adaptive total daily insulin (TDI) estimate and, only on
 * Control-IQ+ Mobis with Control-IQ disabled, a TDI-derived correction factor. 227 carries 8-bit
 * copies of three of these: its byte 20 is {@link #getFilteredGlucose()} capped at 255, its byte
 * 22 is {@link #getTdiEstimate()} and its byte 21 equals {@link #getCorrectionFactor()} whenever
 * that is non-zero.
 *
 * Written on both Control-IQ and non-Control-IQ pumps, with or without a CGM. After a pump
 * reboot the record starts from TDI = the Control-IQ TDI setting, filtered glucose 31 and
 * prediction 20.00 mg/dL until CGM readings arrive. On Control-IQ v7.6 Mobi firmware with
 * Control-IQ disabled every byte is 0. A t:slim X2 firmware from early 2022 writes 227 but not
 * this record.
 *
 * These are pump-internal Control-IQ values: do not use them for dosing or IOB.
 *
 * The class name is provisional: opcode 327 is not in Tandem's cloud event schema or the Mobi
 * app's history log list. Bytes 11-13, 18-19 and 21-25 were 0 in every observed record and are
 * exposed raw.
 */
@HistoryLogProps(
    opCode = 327,
    displayName = "Control-IQ Glucose Model"
)
public class ControlIQGlucoseModelHistoryLog extends HistoryLog {

    private int tdiEstimate;
    private int unknown11;
    private int unknown12;
    private int shortTermPredictedGlucoseRaw;
    private int filteredGlucose;
    private int unknown18;
    private int correctionFactor;
    private int unknown21;
    private int unknown22;
    private int unknown24;

    public ControlIQGlucoseModelHistoryLog() {}
    public ControlIQGlucoseModelHistoryLog(long pumpTimeSec, long sequenceNum, int tdiEstimate, int unknown11, int unknown12, int shortTermPredictedGlucoseRaw, int filteredGlucose, int unknown18, int correctionFactor, int unknown21, int unknown22, int unknown24) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, tdiEstimate, unknown11, unknown12, shortTermPredictedGlucoseRaw, filteredGlucose, unknown18, correctionFactor, unknown21, unknown22, unknown24);
        this.tdiEstimate = tdiEstimate;
        this.unknown11 = unknown11;
        this.unknown12 = unknown12;
        this.shortTermPredictedGlucoseRaw = shortTermPredictedGlucoseRaw;
        this.filteredGlucose = filteredGlucose;
        this.unknown18 = unknown18;
        this.correctionFactor = correctionFactor;
        this.unknown21 = unknown21;
        this.unknown22 = unknown22;
        this.unknown24 = unknown24;
    }

    public int typeId() {
        return 327;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.tdiEstimate = raw[10] & 0xFF;
        this.unknown11 = raw[11] & 0xFF;
        this.unknown12 = Bytes.readShort(raw, 12);
        this.shortTermPredictedGlucoseRaw = Bytes.readShort(raw, 14);
        this.filteredGlucose = Bytes.readShort(raw, 16);
        this.unknown18 = Bytes.readShort(raw, 18);
        this.correctionFactor = raw[20] & 0xFF;
        this.unknown21 = raw[21] & 0xFF;
        this.unknown22 = Bytes.readShort(raw, 22);
        this.unknown24 = Bytes.readShort(raw, 24);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int tdiEstimate, int unknown11, int unknown12, int shortTermPredictedGlucoseRaw, int filteredGlucose, int unknown18, int correctionFactor, int unknown21, int unknown22, int unknown24) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(327, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstByteLittleEndian(tdiEstimate),
            Bytes.firstByteLittleEndian(unknown11),
            Bytes.firstTwoBytesLittleEndian(unknown12),
            Bytes.firstTwoBytesLittleEndian(shortTermPredictedGlucoseRaw),
            Bytes.firstTwoBytesLittleEndian(filteredGlucose),
            Bytes.firstTwoBytesLittleEndian(unknown18),
            Bytes.firstByteLittleEndian(correctionFactor),
            Bytes.firstByteLittleEndian(unknown21),
            Bytes.firstTwoBytesLittleEndian(unknown22),
            Bytes.firstTwoBytesLittleEndian(unknown24)));
    }

    /**
     * The integer part of Control-IQ's adaptive total daily insulin estimate. On Mobis it equals
     * floor() of the new estimate in the AA TDI Estimate Change record (332) written hourly in
     * the same second (4,426 of 4,426 records, two pumps); t:slim X2s change it in hourly 1-3 unit
     * steps without writing 332. After a reboot it restarts from the Control-IQ TDI setting.
     *
     * @return the TDI estimate in whole units per day (0 when the record is all zero)
     */
    public int getTdiEstimate() {
        return tdiEstimate;
    }

    /**
     * Always 0 observed; possibly the high byte of {@link #getTdiEstimate()}.
     *
     * @return raw uint8 at offset 11
     */
    public int getUnknown11() {
        return unknown11;
    }

    /**
     * Always 0 observed.
     *
     * @return raw uint16 at offset 12
     */
    public int getUnknown12() {
        return unknown12;
    }

    /**
     * A glucose prediction in hundredths of mg/dL, with a shorter horizon than the one in 227
     * bytes 14-15: (227 prediction - filtered glucose) = 1.85 x (this - filtered glucose),
     * R&sup2; 0.985-0.995 on three CGM pumps. With a CGM it follows the trend of
     * {@link #getFilteredGlucose()}; without one it falls below it as insulin on board rises.
     * Control-IQ's basal and auto-bolus thresholds (112.5 and 180 mg/dL) line up with 227's
     * prediction, not this one. The horizon is not known (roughly 15 minutes is a guess).
     * Floor 2000 (20.00 mg/dL).
     *
     * @return the raw uint16 at offset 14, mg/dL x 100
     */
    public int getShortTermPredictedGlucoseRaw() {
        return shortTermPredictedGlucoseRaw;
    }

    /**
     * @return {@link #getShortTermPredictedGlucoseRaw()} in mg/dL
     */
    public double getShortTermPredictedGlucoseMgdl() {
        return shortTermPredictedGlucoseRaw / 100.0;
    }

    /**
     * Control-IQ's filtered current glucose. With a CGM it follows the latest reading
     * (r 0.996-0.9998, mean absolute difference 1.3-2.6 mg/dL, four pumps) and is fitted by
     * 0.38 x previous value + 0.62 x latest reading + 0.25 x previous change (R&sup2; above 0.999).
     * Not capped at 255, unlike 227 byte 20. Without a CGM it holds a pump-dependent value; after
     * a reboot it starts at 31.
     *
     * @return the filtered glucose in mg/dL (uint16 at offset 16)
     */
    public int getFilteredGlucose() {
        return filteredGlucose;
    }

    /**
     * Always 0 observed.
     *
     * @return raw uint16 at offset 18
     */
    public int getUnknown18() {
        return unknown18;
    }

    /**
     * A correction factor derived from the TDI estimate, filled only on Control-IQ+ Mobis with
     * Control-IQ disabled; 0 on every t:slim X2 record and on Mobis with Control-IQ enabled.
     * Equal to floor(1800 / TDI estimate) in all 4,425 such records from 2026 on two pumps, capped
     * on one of them at a value equal to its profile ISF. Eight other non-zero records fit
     * floor(1500 / TDI estimate) instead, so the constant is not settled.
     *
     * @return the correction factor in mg/dL per unit, or 0 when not filled
     */
    public int getCorrectionFactor() {
        return correctionFactor;
    }

    /**
     * Always 0 observed.
     *
     * @return raw uint8 at offset 21
     */
    public int getUnknown21() {
        return unknown21;
    }

    /**
     * Always 0 observed.
     *
     * @return raw uint16 at offset 22
     */
    public int getUnknown22() {
        return unknown22;
    }

    /**
     * Always 0 observed.
     *
     * @return raw uint16 at offset 24
     */
    public int getUnknown24() {
        return unknown24;
    }
}
