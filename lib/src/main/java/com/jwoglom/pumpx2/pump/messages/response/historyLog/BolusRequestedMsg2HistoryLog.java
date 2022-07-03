package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

public class BolusRequestedMsg2HistoryLog extends HistoryLog {
    
    private int bolusId;
    private int options;
    private int standardPercent;
    private int duration;
    private int spare1;
    private int isf;
    private int targetBG;
    private boolean userOverride;
    private boolean declinedCorrection;
    private int selectedIOB;
    private int spare2;
    
    public BolusRequestedMsg2HistoryLog() {}
    
    public BolusRequestedMsg2HistoryLog(int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2) {
        this.cargo = buildCargo(bolusId, options, standardPercent, duration, spare1, isf, targetBG, userOverride, declinedCorrection, selectedIOB, spare2);
        this.bolusId = bolusId;
        this.options = options;
        this.standardPercent = standardPercent;
        this.duration = duration;
        this.spare1 = spare1;
        this.isf = isf;
        this.targetBG = targetBG;
        this.userOverride = userOverride;
        this.declinedCorrection = declinedCorrection;
        this.selectedIOB = selectedIOB;
        this.spare2 = spare2;
        
    }

    public int typeId() {
        return 65;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        this.bolusId = Bytes.readShort(raw, 10);
        this.options = raw[12];
        this.standardPercent = raw[13];
        this.duration = Bytes.readShort(raw, 14);
        this.spare1 = Bytes.readShort(raw, 16);
        this.isf = Bytes.readShort(raw, 18);
        this.targetBG = Bytes.readShort(raw, 20);
        this.userOverride = raw[22] != 0;
        this.declinedCorrection = raw[23] != 0;
        this.selectedIOB = raw[24];
        this.spare2 = raw[1];
        
    }

    
    public static byte[] buildCargo(int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2) {
        return Bytes.combine(
            new byte[] { (byte) 65, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            Bytes.firstTwoBytesLittleEndian(bolusId), 
            new byte[]{ (byte) options }, 
            new byte[]{ (byte) standardPercent }, 
            Bytes.firstTwoBytesLittleEndian(duration), 
            Bytes.firstTwoBytesLittleEndian(spare1), 
            Bytes.firstTwoBytesLittleEndian(isf), 
            Bytes.firstTwoBytesLittleEndian(targetBG), 
            new byte[]{ (byte) (userOverride ? 1 : 0) }, 
            new byte[]{ (byte) (declinedCorrection ? 1 : 0) }, 
            new byte[]{ (byte) selectedIOB }, 
            new byte[]{ (byte) spare2 });
    }
    
    public int getBolusId() {
        return bolusId;
    }
    public int getOptions() {
        return options;
    }
    public int getStandardPercent() {
        return standardPercent;
    }
    public int getDuration() {
        return duration;
    }
    public int getSpare1() {
        return spare1;
    }
    public int getIsf() {
        return isf;
    }
    public int getTargetBG() {
        return targetBG;
    }
    public boolean getUserOverride() {
        return userOverride;
    }
    public boolean getDeclinedCorrection() {
        return declinedCorrection;
    }
    public int getSelectedIOB() {
        return selectedIOB;
    }
    public int getSpare2() {
        return spare2;
    }
    
}