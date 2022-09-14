package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 97,
    displayName = "Reminder Parameter Change",
    usedByTidepool = true
)
public class ParamChangeRemSettingsHistoryLog extends HistoryLog {
    
    private int modification;
    private int status;
    private int lowBgThreshold;
    private int highBgThreshold;
    private int siteChangeDays;
    
    public ParamChangeRemSettingsHistoryLog() {}
    public ParamChangeRemSettingsHistoryLog(long pumpTimeSec, long sequenceNum, int modification, int status, int lowBgThreshold, int highBgThreshold, int siteChangeDays) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, modification, status, lowBgThreshold, highBgThreshold, siteChangeDays);
        this.modification = modification;
        this.status = status;
        this.lowBgThreshold = lowBgThreshold;
        this.highBgThreshold = highBgThreshold;
        this.siteChangeDays = siteChangeDays;
        
    }

    public ParamChangeRemSettingsHistoryLog(int modification, int status, int lowBgThreshold, int highBgThreshold, int siteChangeDays) {
        this(0, 0, modification, status, lowBgThreshold, highBgThreshold, siteChangeDays);
    }

    public int typeId() {
        return 97;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.modification = raw[10];
        this.status = raw[11];
        this.lowBgThreshold = Bytes.readShort(raw, 14);
        this.highBgThreshold = Bytes.readShort(raw, 16);
        this.siteChangeDays = raw[18];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int modification, int status, int lowBgThreshold, int highBgThreshold, int siteChangeDays) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{97, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) modification }, 
            new byte[]{ (byte) status }, 
            Bytes.firstTwoBytesLittleEndian(lowBgThreshold), 
            Bytes.firstTwoBytesLittleEndian(highBgThreshold), 
            new byte[]{ (byte) siteChangeDays }));
    }
    public int getModification() {
        return modification;
    }
    public int getStatus() {
        return status;
    }
    public int getLowBgThreshold() {
        return lowBgThreshold;
    }
    public int getHighBgThreshold() {
        return highBgThreshold;
    }
    public int getSiteChangeDays() {
        return siteChangeDays;
    }
    
}