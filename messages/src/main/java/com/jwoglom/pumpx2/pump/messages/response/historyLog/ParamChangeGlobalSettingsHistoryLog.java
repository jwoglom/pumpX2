package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 74,
    displayName = "Global Settings Change",
    usedByTidepool = true
)
public class ParamChangeGlobalSettingsHistoryLog extends HistoryLog {
    
    private int modifiedData;
    private int qbDataStatus;
    private int qbActive;
    private int qbDataEntryType;
    private int qbIncrementUnits;
    private int qbIncrementCarbs;
    private int buttonVolume;
    private int qbVolume;
    private int bolusVolume;
    private int reminderVolume;
    private int alertVolume;
    
    public ParamChangeGlobalSettingsHistoryLog() {}
    public ParamChangeGlobalSettingsHistoryLog(long pumpTimeSec, long sequenceNum, int modifiedData, int qbDataStatus, int qbActive, int qbDataEntryType, int qbIncrementUnits, int qbIncrementCarbs, int buttonVolume, int qbVolume, int bolusVolume, int reminderVolume, int alertVolume) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, modifiedData, qbDataStatus, qbActive, qbDataEntryType, qbIncrementUnits, qbIncrementCarbs, buttonVolume, qbVolume, bolusVolume, reminderVolume, alertVolume);
        this.modifiedData = modifiedData;
        this.qbDataStatus = qbDataStatus;
        this.qbActive = qbActive;
        this.qbDataEntryType = qbDataEntryType;
        this.qbIncrementUnits = qbIncrementUnits;
        this.qbIncrementCarbs = qbIncrementCarbs;
        this.buttonVolume = buttonVolume;
        this.qbVolume = qbVolume;
        this.bolusVolume = bolusVolume;
        this.reminderVolume = reminderVolume;
        this.alertVolume = alertVolume;
        
    }

    public ParamChangeGlobalSettingsHistoryLog(int modifiedData, int qbDataStatus, int qbActive, int qbDataEntryType, int qbIncrementUnits, int qbIncrementCarbs, int buttonVolume, int qbVolume, int bolusVolume, int reminderVolume, int alertVolume) {
        this(0, 0, modifiedData, qbDataStatus, qbActive, qbDataEntryType, qbIncrementUnits, qbIncrementCarbs, buttonVolume, qbVolume, bolusVolume, reminderVolume, alertVolume);
    }

    public int typeId() {
        return 74;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.modifiedData = raw[10];
        this.qbDataStatus = raw[11];
        this.qbActive = raw[12];
        this.qbDataEntryType = raw[13];
        this.qbIncrementUnits = Bytes.readShort(raw, 14);
        this.qbIncrementCarbs = Bytes.readShort(raw, 16);
        this.buttonVolume = raw[18];
        this.qbVolume = raw[19];
        this.bolusVolume = raw[20];
        this.reminderVolume = raw[21];
        this.alertVolume = raw[22];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int modifiedData, int qbDataStatus, int qbActive, int qbDataEntryType, int qbIncrementUnits, int qbIncrementCarbs, int buttonVolume, int qbVolume, int bolusVolume, int reminderVolume, int alertVolume) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{74, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) modifiedData }, 
            new byte[]{ (byte) qbDataStatus }, 
            new byte[]{ (byte) qbActive }, 
            new byte[]{ (byte) qbDataEntryType }, 
            Bytes.firstTwoBytesLittleEndian(qbIncrementUnits), 
            Bytes.firstTwoBytesLittleEndian(qbIncrementCarbs), 
            new byte[]{ (byte) buttonVolume }, 
            new byte[]{ (byte) qbVolume }, 
            new byte[]{ (byte) bolusVolume }, 
            new byte[]{ (byte) reminderVolume }, 
            new byte[]{ (byte) alertVolume }));
    }
    public int getModifiedData() {
        return modifiedData;
    }
    public int getQbDataStatus() {
        return qbDataStatus;
    }
    public int getQbActive() {
        return qbActive;
    }
    public int getQbDataEntryType() {
        return qbDataEntryType;
    }
    public int getQbIncrementUnits() {
        return qbIncrementUnits;
    }
    public int getQbIncrementCarbs() {
        return qbIncrementCarbs;
    }
    public int getButtonVolume() {
        return buttonVolume;
    }
    public int getQbVolume() {
        return qbVolume;
    }
    public int getBolusVolume() {
        return bolusVolume;
    }
    public int getReminderVolume() {
        return reminderVolume;
    }
    public int getAlertVolume() {
        return alertVolume;
    }
    
}