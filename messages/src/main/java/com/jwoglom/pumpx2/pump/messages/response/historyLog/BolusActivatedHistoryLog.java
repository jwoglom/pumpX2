package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 55,
    displayName = "Bolus Activated",
    usedByTidepool = true
)
public class BolusActivatedHistoryLog extends HistoryLog {
    
    private int bolusId;
    private float iob;
    private float bolusSize;
    
    public BolusActivatedHistoryLog() {}
    public BolusActivatedHistoryLog(long pumpTimeSec, long sequenceNum, int bolusId, float iob, float bolusSize) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, bolusId, iob, bolusSize);
        this.bolusId = bolusId;
        this.iob = iob;
        this.bolusSize = bolusSize;
        
    }

    public BolusActivatedHistoryLog(int bolusId, float iob, float bolusSize) {
        this(0, 0, bolusId, iob, bolusSize);
    }

    public int typeId() {
        return 55;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.bolusId = Bytes.readShort(raw, 10);
        this.iob = Bytes.readFloat(raw, 14);
        this.bolusSize = Bytes.readFloat(raw, 18);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int bolusId, float iob, float bolusSize) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{55, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            Bytes.toFloat(iob), 
            Bytes.toFloat(bolusSize)));
    }
    public int getBolusId() {
        return bolusId;
    }
    public float getIob() {
        return iob;
    }
    public float getBolusSize() {
        return bolusSize;
    }
    
}