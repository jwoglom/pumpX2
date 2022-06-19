package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

public class BolexCompletedHistoryLog extends HistoryLog {
    
    private int completionStatus;
    private int bolusId;
    private float iob;
    private float insulinDelivered;
    private float insulinRequested;
    
    public BolexCompletedHistoryLog() {}
    
    public BolexCompletedHistoryLog(int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested) {
        this.cargo = buildCargo(completionStatus, bolusId, iob, insulinDelivered, insulinRequested);
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
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        this.completionStatus = Bytes.readShort(raw, 10);
        this.bolusId = Bytes.readShort(raw, 12);
        this.iob = Bytes.readFloat(raw, 14);
        this.insulinDelivered = Bytes.readFloat(raw, 18);
        this.insulinRequested = Bytes.readFloat(raw, 22);
        
    }

    
    public static byte[] buildCargo(int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested) {
        return Bytes.combine(
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