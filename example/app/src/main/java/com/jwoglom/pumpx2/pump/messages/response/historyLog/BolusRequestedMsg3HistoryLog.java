package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

public class BolusRequestedMsg3HistoryLog extends HistoryLog {
    
    private int bolusId;
    private int spare;
    private float foodBolusSize;
    private float correctionBolusSize;
    private float totalBolusSize;
    
    public BolusRequestedMsg3HistoryLog() {}
    
    public BolusRequestedMsg3HistoryLog(int bolusId, int spare, float foodBolusSize, float correctionBolusSize, float totalBolusSize) {
        this.cargo = buildCargo(bolusId, spare, foodBolusSize, correctionBolusSize, totalBolusSize);
        this.bolusId = bolusId;
        this.spare = spare;
        this.foodBolusSize = foodBolusSize;
        this.correctionBolusSize = correctionBolusSize;
        this.totalBolusSize = totalBolusSize;
        
    }

    public int typeId() {
        return 66;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        this.bolusId = Bytes.readShort(raw, 10);
        this.spare = Bytes.readShort(raw, 12);
        this.foodBolusSize = Bytes.readFloat(raw, 14);
        this.correctionBolusSize = Bytes.readFloat(raw, 18);
        this.totalBolusSize = Bytes.readFloat(raw, 22);
        
    }

    
    public static byte[] buildCargo(int bolusId, int spare, float foodBolusSize, float correctionBolusSize, float totalBolusSize) {
        return Bytes.combine(
            new byte[] { (byte) 66, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            Bytes.firstTwoBytesLittleEndian(spare), 
            Bytes.toFloat(foodBolusSize), 
            Bytes.toFloat(correctionBolusSize), 
            Bytes.toFloat(totalBolusSize));
    }
    
    public int getBolusId() {
        return bolusId;
    }
    public int getSpare() {
        return spare;
    }
    public float getFoodBolusSize() {
        return foodBolusSize;
    }
    public float getCorrectionBolusSize() {
        return correctionBolusSize;
    }
    public float getTotalBolusSize() {
        return totalBolusSize;
    }
    
}