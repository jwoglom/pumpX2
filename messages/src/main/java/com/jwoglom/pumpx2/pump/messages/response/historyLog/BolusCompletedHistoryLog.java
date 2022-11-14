package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBolusStatusAbstractResponse;

@HistoryLogProps(
    opCode = 20,
    displayName = "Bolus Completed",
    usedByAndroid = true,
    usedByTidepool = true
)
public class BolusCompletedHistoryLog extends HistoryLog {
    
    private int completionStatusId;
    private int bolusId;
    private float iob;
    private float insulinDelivered;
    private float insulinRequested;
    
    public BolusCompletedHistoryLog() {}
    
    public BolusCompletedHistoryLog(long pumpTimeSec, long sequenceNum, int completionStatusId, int bolusId, float iob, float insulinDelivered, float insulinRequested) {
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, completionStatusId, bolusId, iob, insulinDelivered, insulinRequested);
        this.pumpTimeSec = pumpTimeSec;
        this.sequenceNum = sequenceNum;
        this.completionStatusId = completionStatusId;
        this.bolusId = bolusId;
        this.iob = iob;
        this.insulinDelivered = insulinDelivered;
        this.insulinRequested = insulinRequested;
        
    }

    public int typeId() {
        return 20;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.completionStatusId = Bytes.readShort(raw, 10);
        this.bolusId = Bytes.readShort(raw, 12);
        this.iob = Bytes.readFloat(raw, 14);
        this.insulinDelivered = Bytes.readFloat(raw, 18);
        this.insulinRequested = Bytes.readFloat(raw, 22);
        
    }

    
    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested) {
        return Bytes.combine(
            new byte[]{20, 0},
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

    /**
     * @return whether the bolus was completed fully or stopped
     */
    public LastBolusStatusAbstractResponse.BolusStatus getCompletionStatus() {
        return LastBolusStatusAbstractResponse.BolusStatus.fromId(completionStatusId);
    }

    /**
     * @return the ID of the bolus
     */
    public int getBolusId() {
        return bolusId;
    }

    /**
     * @return the current insulin on board
     */
    public float getIob() {
        return iob;
    }

    /**
     * @return the amount of insulin delivered, in real units. Note that due to ieee float
     * precision, even if the bolus was completed fully this may differ from the units requested
     */
    public float getInsulinDelivered() {
        return insulinDelivered;
    }

    /**
     * @return the amount of insulin requested, in real units
     */
    public float getInsulinRequested() {
        return insulinRequested;
    }
    
}