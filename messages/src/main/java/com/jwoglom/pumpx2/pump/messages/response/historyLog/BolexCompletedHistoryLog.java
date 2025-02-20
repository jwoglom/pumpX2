package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 21,
    displayName = "Extended Bolus Portion Complete",
    usedByAndroid = true
)
public class BolexCompletedHistoryLog extends HistoryLog {
    
    private int completionStatus;
    private int bolusId;
    private float iob;
    private float insulinDelivered;
    private float insulinRequested;
    
    public BolexCompletedHistoryLog() {}
    
    public BolexCompletedHistoryLog(long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, completionStatus, bolusId, iob, insulinDelivered, insulinRequested);
        this.pumpTimeSec = pumpTimeSec;
        this.sequenceNum = sequenceNum;
        this.completionStatus = completionStatus;
        this.bolusId = bolusId;
        this.iob = iob;
        this.insulinDelivered = insulinDelivered;
        this.insulinRequested = insulinRequested;
        
    }

    public int typeId() {
        return 21;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.completionStatus = Bytes.readShort(raw, 10);
        this.bolusId = Bytes.readShort(raw, 12);
        this.iob = Bytes.readFloat(raw, 14);
        this.insulinDelivered = Bytes.readFloat(raw, 18);
        this.insulinRequested = Bytes.readFloat(raw, 22);
        
    }

    
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested) {
        return Bytes.combine(
            new byte[]{21, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(completionStatus), 
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            Bytes.toFloat(iob), 
            Bytes.toFloat(insulinDelivered), 
            Bytes.toFloat(insulinRequested));
    }
    
    public int getCompletionStatus() {
        return completionStatus;
    }
    public int getBolusId() {
        return bolusId;
    }
    public float getIob() {
        return iob;
    }
    public float getInsulinDelivered() {
        return insulinDelivered;
    }
    public float getInsulinRequested() {
        return insulinRequested;
    }
    
}