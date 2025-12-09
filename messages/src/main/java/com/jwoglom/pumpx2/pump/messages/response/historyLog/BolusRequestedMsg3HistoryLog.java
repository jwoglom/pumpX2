package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 66,
    displayName = "Bolus Requested 3/3",
    internalName = "LID_BOLUS_REQUESTED_MSG3",
    usedByAndroid = true,
    usedByTidepool = true
)
public class BolusRequestedMsg3HistoryLog extends HistoryLog {
    
    private int bolusId;
    private int spare;
    private float foodBolusSize;
    private float correctionBolusSize;
    private float totalBolusSize;
    
    public BolusRequestedMsg3HistoryLog() {}
    
    public BolusRequestedMsg3HistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, int spare, float foodBolusSize, float correctionBolusSize, float totalBolusSize) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, spare, foodBolusSize, correctionBolusSize, totalBolusSize);
        this.pumpTimeSec = pumpTimeSec;
        this.sequenceNum = sequenceNum;
        this.bolusId = bolusId;
        this.spare = spare;
        this.foodBolusSize = foodBolusSize;
        this.correctionBolusSize = correctionBolusSize;
        this.totalBolusSize = totalBolusSize;
    }

    public BolusRequestedMsg3HistoryLog(int bolusId, int spare, float foodBolusSize, float correctionBolusSize, float totalBolusSize) {
        this(0, 0, bolusId, spare, foodBolusSize, correctionBolusSize, totalBolusSize);
    }

    public int typeId() {
        return 66;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusId = Bytes.readShort(raw, 10);
        this.spare = Bytes.readShort(raw, 12);
        this.foodBolusSize = Bytes.readFloat(raw, 14);
        this.correctionBolusSize = Bytes.readFloat(raw, 18);
        this.totalBolusSize = Bytes.readFloat(raw, 22);
        
    }

    
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, int spare, float foodBolusSize, float correctionBolusSize, float totalBolusSize) {
        return Bytes.combine(
            new byte[] { (byte) 66, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            Bytes.firstTwoBytesLittleEndian(spare), 
            Bytes.toFloat(foodBolusSize), 
            Bytes.toFloat(correctionBolusSize), 
            Bytes.toFloat(totalBolusSize));
    }

    /**
     * @return bolus ID
     */
    public int getBolusId() {
        return bolusId;
    }
    public int getSpare() {
        return spare;
    }

    /**
     * @return the portion of the bolus amount relating to food from carbs
     */
    public float getFoodBolusSize() {
        return foodBolusSize;
    }

    /**
     * @return the portion of the bolus amount relating to the correction bolus from BG+IOB
     */
    public float getCorrectionBolusSize() {
        return correctionBolusSize;
    }

    /**
     * @return the total bolus amount which is scheduled to be delivered following this message.
     * note that this may not be correctionBolusSize+foodBolusSize if the amounts are overridden,
     * or due to ieee float arithmetic
     */
    public float getTotalBolusSize() {
        return totalBolusSize;
    }
}