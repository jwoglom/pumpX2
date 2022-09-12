package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

public class UnknownHistoryLog extends HistoryLog {
    private int typeId = 0;
    public UnknownHistoryLog() {}

    public UnknownHistoryLog(byte[] raw) {
        this.cargo = raw;
        
    }

    public int typeId() {
        return typeId;
    }

    public void parse(byte[] raw) {
        this.cargo = raw;
        this.typeId = Bytes.readShort(raw, 0) & 4095;
        parseBase(raw);
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
    
}