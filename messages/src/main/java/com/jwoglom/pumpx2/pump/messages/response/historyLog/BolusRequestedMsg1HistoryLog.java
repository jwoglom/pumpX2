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

    public BolusRequestedMsg1HistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int bolusTypeId, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio, int logGeneration) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, bolusTypeId, correctionBolusIncluded, carbAmount, bg, iob, carbRatio, logGeneration);
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

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio, int logGeneration) {
        return Bytes.combine(
            HistoryLog.typeIdBytes(64, logGeneration),
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
     * <p>Note that this is a scalar enum, not the bitmask used by
     * {@link BolusDeliveryHistoryLog#getBolusTypes()}. Decoding it as a bitmask is contradicted by
     * the data: a raw value of 3 would decode to FOOD1|CORRECTION, but records carrying 3 also
     * report {@link #getCorrectionBolusIncluded()} false and a correction bolus size of zero in
     * {@link BolusRequestedMsg3HistoryLog}. As a scalar, 3 is {@link BolusType#REMOTE}, which is
     * consistent with those records having been commanded over Bluetooth.
     */
    public BolusType getBolusType() {
        return BolusType.fromId(bolusTypeId);
    }

    /**
     * The way in which a bolus was requested, as reported by this log only.
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
     * <p>The ordering of this field and {@link #getBg()} is the one field pair in this log which
     * has not been independently confirmed against a capture: every record examined so far was
     * either commanded remotely or entered without a fingerstick, leaving both fields zero. The
     * offsets either side of the pair are confirmed.
     */
    public int getCarbAmount() {
        return carbAmount;
    }

    /**
     * @return BG in mg/dL. See the note on {@link #getCarbAmount()} regarding this field's offset.
     */
    public int getBg() {
        return bg;
    }

    /**
     * @return current insulin on board.
     *
     * <p>This field is zero on some pumps which report a nonzero IOB for the same bolus in
     * {@link BolusActivatedHistoryLog} and {@link BolusCompletedHistoryLog}. Prefer those logs as
     * the source of IOB.
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