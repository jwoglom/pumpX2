package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.util.Set;

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
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, bolusTypeId, correctionBolusIncluded, carbAmount, bg, iob, carbRatio);
        this.bolusId = bolusId;
        this.bolusTypeId = bolusTypeId; // probably the same bolusType enum as BolusDeliveryHistoryLog
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
        return Bytes.combine(
            new byte[]{64, 0},
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
     * @return Bolus types
     */
    public Set<BolusDeliveryHistoryLog.BolusType> getBolusType() {
        return BolusDeliveryHistoryLog.BolusType.fromBitmask(bolusTypeId);
    }

    /**
     * @return if the correction bolus is included
     */
    public boolean getCorrectionBolusIncluded() {
        return correctionBolusIncluded;
    }

    /**
     * @return carbs in grams
     */
    public int getCarbAmount() {
        return carbAmount;
    }

    /**
     * @return BG in mg/dL
     */
    public int getBg() {
        return bg;
    }

    /**
     * @return current insulin on board
     */
    public float getIob() {
        return iob;
    }

    /**
     * @return carb ratio from the insulin delivery profile
     */
    public long getCarbRatio() {
        return carbRatio;
    }
}