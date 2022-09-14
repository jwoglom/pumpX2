package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 160,
    displayName = "CGM Calibration",
    usedByTidepool = true
)
public class CgmCalibrationHistoryLog extends HistoryLog {
    
    private long currentTime;
    private long timestamp;
    private long calTimestamp;
    private int value;
    private int currentDisplayValue;
    
    public CgmCalibrationHistoryLog() {}
    public CgmCalibrationHistoryLog(long pumpTimeSec, long sequenceNum, long currentTime, long timestamp, long calTimestamp, int value, int currentDisplayValue) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentTime, timestamp, calTimestamp, value, currentDisplayValue);
        this.currentTime = currentTime;
        this.timestamp = timestamp;
        this.calTimestamp = calTimestamp;
        this.value = value;
        this.currentDisplayValue = currentDisplayValue;
        
    }

    public CgmCalibrationHistoryLog(long currentTime, long timestamp, long calTimestamp, int value, int currentDisplayValue) {
        this(0, 0, currentTime, timestamp, calTimestamp, value, currentDisplayValue);
    }

    public int typeId() {
        return 160;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.currentTime = Bytes.readUint32(raw, 10);
        this.timestamp = Bytes.readUint32(raw, 14);
        this.calTimestamp = Bytes.readUint32(raw, 18);
        this.value = Bytes.readShort(raw, 22);
        this.currentDisplayValue = Bytes.readShort(raw, 24);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long currentTime, long timestamp, long calTimestamp, int value, int currentDisplayValue) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{-96, 0}, // (byte) 160
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(currentTime), 
            Bytes.toUint32(timestamp), 
            Bytes.toUint32(calTimestamp), 
            Bytes.firstTwoBytesLittleEndian(value), 
            Bytes.firstTwoBytesLittleEndian(currentDisplayValue)));
    }
    public long getCurrentTime() {
        return currentTime;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public long getCalTimestamp() {
        return calTimestamp;
    }
    public int getValue() {
        return value;
    }
    public int getCurrentDisplayValue() {
        return currentDisplayValue;
    }
    
}