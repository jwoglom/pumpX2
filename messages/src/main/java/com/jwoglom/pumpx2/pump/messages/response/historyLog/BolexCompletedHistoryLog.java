package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBolusStatusAbstractResponse;

@HistoryLogProps(
    opCode = 21,
    displayName = "Extended Bolus Portion Complete",
    internalName = "LID_BOLEX_COMPLETED",
    usedByAndroid = true
)
public class BolexCompletedHistoryLog extends HistoryLog {
    
    private int completionStatusId;
    private LastBolusStatusAbstractResponse.BolusStatus completionStatus;
    private int bolusId;
    private float iob;
    private float insulinDelivered;
    private float insulinRequested;
    
    public BolexCompletedHistoryLog() {}
    
    public BolexCompletedHistoryLog(long pumpTimeSec, long sequenceNum, int completionStatusId, int bolusId, float iob, float insulinDelivered, float insulinRequested) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, completionStatusId, bolusId, iob, insulinDelivered, insulinRequested);
        this.pumpTimeSec = pumpTimeSec;
        this.sequenceNum = sequenceNum;
        this.completionStatusId = completionStatusId;
        this.completionStatus = LastBolusStatusAbstractResponse.BolusStatus.fromId(completionStatusId);
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
        this.completionStatusId = Bytes.readShort(raw, 10);
        this.completionStatus = LastBolusStatusAbstractResponse.BolusStatus.fromId(completionStatusId);
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
    
    public int getCompletionStatusId() {
        return completionStatusId;
    }
    public LastBolusStatusAbstractResponse.BolusStatus getCompletionStatus() {
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