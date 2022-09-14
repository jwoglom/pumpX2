package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 93,
    displayName = "Correction Declined",
    usedByTidepool = true
)
public class CorrectionDeclinedHistoryLog extends HistoryLog {
    
    private int bg;
    private int bolusId;
    private float iob;
    private int targetBg;
    private int isf;
    
    public CorrectionDeclinedHistoryLog() {}
    public CorrectionDeclinedHistoryLog(long pumpTimeSec, long sequenceNum, int bg, int bolusId, float iob, int targetBg, int isf) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bg, bolusId, iob, targetBg, isf);
        this.bg = bg;
        this.bolusId = bolusId;
        this.iob = iob;
        this.targetBg = targetBg;
        this.isf = isf;
        
    }

    public CorrectionDeclinedHistoryLog(int bg, int bolusId, float iob, int targetBg, int isf) {
        this(0, 0, bg, bolusId, iob, targetBg, isf);
    }

    public int typeId() {
        return 93;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bg = Bytes.readShort(raw, 10);
        this.bolusId = Bytes.readShort(raw, 12);
        this.iob = Bytes.readFloat(raw, 14);
        this.targetBg = Bytes.readShort(raw, 18);
        this.isf = Bytes.readShort(raw, 20);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bg, int bolusId, float iob, int targetBg, int isf) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{93, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bg), 
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            Bytes.toFloat(iob), 
            Bytes.firstTwoBytesLittleEndian(targetBg), 
            Bytes.firstTwoBytesLittleEndian(isf)));
    }
    public int getBg() {
        return bg;
    }
    public int getBolusId() {
        return bolusId;
    }
    public float getIob() {
        return iob;
    }
    public int getTargetBg() {
        return targetBg;
    }
    public int getIsf() {
        return isf;
    }
    
}