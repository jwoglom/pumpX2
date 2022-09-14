package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 96,
    displayName = "Reminder Time Based Parameter Change",
    usedByTidepool = true
)
public class ParamChangeReminderHistoryLog extends HistoryLog {
    
    private int modification;
    private int reminderId;
    private int status;
    private int enable;
    private long frequencyMinutes;
    private int startTime;
    private int endTime;
    private int activeDays;
    
    public ParamChangeReminderHistoryLog() {}
    public ParamChangeReminderHistoryLog(long pumpTimeSec, long sequenceNum, int modification, int reminderId, int status, int enable, long frequencyMinutes, int startTime, int endTime, int activeDays) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, modification, reminderId, status, enable, frequencyMinutes, startTime, endTime, activeDays);
        this.modification = modification;
        this.reminderId = reminderId;
        this.status = status;
        this.enable = enable;
        this.frequencyMinutes = frequencyMinutes;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activeDays = activeDays;
        
    }

    public ParamChangeReminderHistoryLog(int modification, int reminderId, int status, int enable, long frequencyMinutes, int startTime, int endTime, int activeDays) {
        this(0, 0, modification, reminderId, status, enable, frequencyMinutes, startTime, endTime, activeDays);
    }

    public int typeId() {
        return 96;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.modification = raw[10];
        this.reminderId = raw[11];
        this.status = raw[12];
        this.enable = raw[13];
        this.frequencyMinutes = Bytes.readUint32(raw, 14);
        this.startTime = Bytes.readShort(raw, 18);
        this.endTime = Bytes.readShort(raw, 20);
        this.activeDays = raw[22];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int modification, int reminderId, int status, int enable, long frequencyMinutes, int startTime, int endTime, int activeDays) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{96, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) modification }, 
            new byte[]{ (byte) reminderId }, 
            new byte[]{ (byte) status }, 
            new byte[]{ (byte) enable }, 
            Bytes.toUint32(frequencyMinutes), 
            Bytes.firstTwoBytesLittleEndian(startTime), 
            Bytes.firstTwoBytesLittleEndian(endTime), 
            new byte[]{ (byte) activeDays }));
    }
    public int getModification() {
        return modification;
    }
    public int getReminderId() {
        return reminderId;
    }
    public int getStatus() {
        return status;
    }
    public int getEnable() {
        return enable;
    }
    public long getFrequencyMinutes() {
        return frequencyMinutes;
    }
    public int getStartTime() {
        return startTime;
    }
    public int getEndTime() {
        return endTime;
    }
    public int getActiveDays() {
        return activeDays;
    }
    
}