package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 59,
    displayName = "Extended Bolus Activated",
    internalName = "LID_BOLEX_ACTIVATED",
    usedByTidepool = true
)
public class BolexActivatedHistoryLog extends HistoryLog {
    
    private int bolusId;
    private float iob;
    private float bolexSize;
    
    public BolexActivatedHistoryLog() {}
    public BolexActivatedHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, float iob, float bolexSize) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, iob, bolexSize);
        this.bolusId = bolusId;
        this.iob = iob;
        this.bolexSize = bolexSize;
        
    }

    public BolexActivatedHistoryLog(int bolusId, float iob, float bolexSize) {
        this(0, 0, bolusId, iob, bolexSize);
    }

    public int typeId() {
        return 59;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusId = Bytes.readShort(raw, 10);
        this.iob = Bytes.readFloat(raw, 14);
        this.bolexSize = Bytes.readFloat(raw, 18);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, float iob, float bolexSize) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{59, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            Bytes.toFloat(iob), 
            Bytes.toFloat(bolexSize)));
    }
    public int getBolusId() {
        return bolusId;
    }
    public float getIob() {
        return iob;
    }
    public float getBolexSize() {
        return bolexSize;
    }
    
}