package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

public class CGMHistoryLog extends HistoryLog {
    
    private int glucoseValueStatus;
    private int cgmDataType;
    private int rate;
    private int algorithmState;
    private int rssi;
    private int currentGlucoseDisplayValue;
    private long timeStampSeconds;
    private int egvInfoBitmask;
    private int interval;
    
    public CGMHistoryLog() {}
    
    public CGMHistoryLog(int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval) {
        this.cargo = buildCargo(glucoseValueStatus, cgmDataType, rate, algorithmState, rssi, currentGlucoseDisplayValue, timeStampSeconds, egvInfoBitmask, interval);
        this.glucoseValueStatus = glucoseValueStatus;
        this.cgmDataType = cgmDataType;
        this.rate = rate;
        this.algorithmState = algorithmState;
        this.rssi = rssi;
        this.currentGlucoseDisplayValue = currentGlucoseDisplayValue;
        this.timeStampSeconds = timeStampSeconds;
        this.egvInfoBitmask = egvInfoBitmask;
        this.interval = interval;
        
    }

    public int typeId() {
        return 256;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        this.glucoseValueStatus = Bytes.readShort(raw, 10);
        this.cgmDataType = raw[12];
        this.rate = raw[13];
        this.algorithmState = raw[14];
        this.rssi = raw[15];
        this.currentGlucoseDisplayValue = Bytes.readShort(raw, 16);
        this.timeStampSeconds = Bytes.readUint32(raw, 18);
        this.egvInfoBitmask = Bytes.readShort(raw, 22);
        this.interval = raw[24];
        
    }

    
    public static byte[] buildCargo(int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(glucoseValueStatus), 
            new byte[]{ (byte) cgmDataType }, 
            new byte[]{ (byte) rate }, 
            new byte[]{ (byte) algorithmState }, 
            new byte[]{ (byte) rssi }, 
            Bytes.firstTwoBytesLittleEndian(currentGlucoseDisplayValue), 
            Bytes.toUint32(timeStampSeconds), 
            Bytes.firstTwoBytesLittleEndian(egvInfoBitmask), 
            new byte[]{ (byte) interval });
    }
    
    public int getGlucoseValueStatus() {
        return glucoseValueStatus;
    }
    public int getCgmDataType() {
        return cgmDataType;
    }
    public int getRate() {
        return rate;
    }
    public int getAlgorithmState() {
        return algorithmState;
    }
    public int getRssi() {
        return rssi;
    }
    public int getCurrentGlucoseDisplayValue() {
        return currentGlucoseDisplayValue;
    }
    public long getTimeStampSeconds() {
        return timeStampSeconds;
    }
    public int getEgvInfoBitmask() {
        return egvInfoBitmask;
    }
    public int getInterval() {
        return interval;
    }
    
}