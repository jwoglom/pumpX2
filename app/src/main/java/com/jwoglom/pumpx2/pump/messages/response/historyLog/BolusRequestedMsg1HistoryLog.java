package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

public class BolusRequestedMsg1HistoryLog extends HistoryLog {
    
    private int bolusId;
    private int bolusType;
    private boolean correctionBolusIncluded;
    private int carbAmount;
    private int bg;
    private float iob;
    private long carbRatio;
    
    public BolusRequestedMsg1HistoryLog() {}
    
    public BolusRequestedMsg1HistoryLog(int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio) {
        this.cargo = buildCargo(bolusId, bolusType, correctionBolusIncluded, carbAmount, bg, iob, carbRatio);
        this.bolusId = bolusId;
        this.bolusType = bolusType; // probably the same bolusType enum as BolusDeliveryHistoryLog
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
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        this.bolusId = Bytes.readShort(raw, 10);
        this.bolusType = raw[12];
        this.correctionBolusIncluded = raw[13] != 0;
        this.carbAmount = Bytes.readShort(raw, 14);
        this.bg = Bytes.readShort(raw, 16);
        this.iob = Bytes.readFloat(raw, 18);
        this.carbRatio = Bytes.readUint32(raw, 22);
        
    }

    
    public static byte[] buildCargo(int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio) {
        return Bytes.combine(
            new byte[]{64, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            new byte[]{ (byte) bolusType }, 
            new byte[]{ (byte) (correctionBolusIncluded ? 1 : 0) }, 
            Bytes.firstTwoBytesLittleEndian(carbAmount), 
            Bytes.firstTwoBytesLittleEndian(bg), 
            Bytes.toFloat(iob), 
            Bytes.toUint32(carbRatio));
    }
    
    public int getBolusId() {
        return bolusId;
    }
    public int getBolusType() {
        return bolusType;
    }
    public boolean getCorrectionBolusIncluded() {
        return correctionBolusIncluded;
    }
    public int getCarbAmount() {
        return carbAmount;
    }
    public int getBg() {
        return bg;
    }
    public float getIob() {
        return iob;
    }
    public long getCarbRatio() {
        return carbRatio;
    }
    
}