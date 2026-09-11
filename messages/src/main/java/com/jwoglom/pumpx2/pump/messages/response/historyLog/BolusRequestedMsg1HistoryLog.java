package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 64,
    displayName = "Bolus Requested 1/3",
    internalName = "LID_BOLUS_REQUESTED_MSG1",
    usedByAndroid = true,
    usedByTidepool = true
)
public class BolusRequestedMsg1HistoryLog extends HistoryLog {
    
    private int bolusId;
    private int bolusTypeId;
    private boolean correctionBolusIncluded;
    private int carbAmount;
    private int bg;
    private float iob;
    private long carbRatio;
    
    public BolusRequestedMsg1HistoryLog() {}
    
    public BolusRequestedMsg1HistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int bolusTypeId, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio) {
        this(pumpTimeSec, sequenceNum, bolusId, bolusTypeId, correctionBolusIncluded, carbAmount, bg, iob, carbRatio, 0);
    }

    public BolusRequestedMsg1HistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int bolusTypeId, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio, int headerHighNibble) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, bolusTypeId, correctionBolusIncluded, carbAmount, bg, iob, carbRatio, headerHighNibble);
        this.bolusId = bolusId;
        this.bolusTypeId = bolusTypeId;
        this.correctionBolusIncluded = correctionBolusIncluded;
        this.carbAmount = carbAmount;
        this.bg = bg;
        this.iob = iob;
        this.carbRatio = carbRatio;
        
    }

    public int typeId() {
        return 64;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusId = Bytes.readShort(raw, 10);
        this.bolusTypeId = raw[12];
        this.correctionBolusIncluded = raw[13] != 0;
        this.carbAmount = Bytes.readShort(raw, 14);
        this.bg = Bytes.readShort(raw, 16);
        this.iob = Bytes.readFloat(raw, 18);
        this.carbRatio = Bytes.readUint32(raw, 22);
        
    }

    
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio) {
        return buildCargo(pumpTimeSec, sequenceNum, bolusId, bolusType, correctionBolusIncluded, carbAmount, bg, iob, carbRatio, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio, int headerHighNibble) {
        return Bytes.combine(
            HistoryLog.typeIdBytes(64, headerHighNibble),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            new byte[]{ (byte) bolusType }, 
            new byte[]{ (byte) (correctionBolusIncluded ? 1 : 0) }, 
            Bytes.firstTwoBytesLittleEndian(carbAmount), 
            Bytes.firstTwoBytesLittleEndian(bg), 
            Bytes.toFloat(iob), 
            Bytes.toUint32(carbRatio));
    }

    /**
     * @return the ID of the bolus operation
     */
    public int getBolusId() {
        return bolusId;
    }

    public int getBolusTypeId() {
        return bolusTypeId;
    }

    /**
     * @return how the bolus was requested, or null if the raw value is not recognized.
     *
     * <p>Treated as a scalar enum rather than the bitmask used by
     * {@link BolusDeliveryHistoryLog#getBolusTypes()}. <b>Validated against a 48k-record real-pump
     * capture:</b> cross-correlating this field with the bolus's delivery source (as reported by
     * {@link BolusDeliveryHistoryLog.BolusSource}) by bolus ID showed 0 ({@code INSULIN}) agreeing
     * with {@code QUICK_BOLUS} on 25/25 boluses and 3 ({@code REMOTE}) agreeing with
     * {@code BLUETOOTH_REMOTE_BOLUS} on 118/118 boluses. Raw values 1 ({@code CARB}) and 2
     * ({@code AUTOMATIC_CORRECTION}) were not observed in the capture and remain unverified.
     */
    public BolusType getBolusType() {
        return BolusType.fromId(bolusTypeId);
    }

    /**
     * The way in which a bolus was requested, as reported by this log only.
     *
     * <p>Value names ported from tconnectsync and not independently verified here; see
     * {@link #getBolusType()}.
     *
     * <p>Deliberately distinct from {@link BolusDeliveryHistoryLog.BolusType}, which is a bitmask
     * of the components making up a bolus and is a different field with a different encoding.
     */
    public enum BolusType {
        INSULIN(0),
        CARB(1),
        AUTOMATIC_CORRECTION(2),
        REMOTE(3),
        ;

        private final int id;
        BolusType(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static BolusType fromId(int id) {
            for (BolusType b : values()) {
                if (b.id() == id) {
                    return b;
                }
            }
            return null;
        }
    }

    /**
     * @return if the correction bolus is included
     */
    public boolean getCorrectionBolusIncluded() {
        return correctionBolusIncluded;
    }

    /**
     * @return carbs in grams.
     *
     * <p>The ordering of this field (offset 14) and {@link #getBg()} (offset 16) was validated
     * against a 48k-record real-pump capture: offset 14 held carb-gram values, while offset 16
     * held plausible BG values in mg/dL across 143/143 correlated records.
     */
    public int getCarbAmount() {
        return carbAmount;
    }

    /**
     * @return BG in mg/dL (offset 16). See the note on {@link #getCarbAmount()}; this ordering was
     * validated against a 48k-record real-pump capture.
     */
    public int getBg() {
        return bg;
    }

    /**
     * @return current insulin on board.
     *
     * <p>The analysis attached to the linked issue reports this field reading zero on records
     * whose corresponding {@link BolusActivatedHistoryLog} and {@link BolusCompletedHistoryLog}
     * carry a nonzero IOB, and recommends preferring those logs as the source of IOB. Not
     * reproduced here; the records committed to this repository carry a nonzero value.
     */
    public float getIob() {
        return iob;
    }

    /**
     * @return carb ratio from the insulin delivery profile, in thousandths of a gram per unit
     */
    public long getCarbRatio() {
        return carbRatio;
    }

    /**
     * @return carb ratio from the insulin delivery profile, in grams per unit
     */
    public double getCarbRatioGramsPerUnit() {
        return carbRatio / 1000.0;
    }
}