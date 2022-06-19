package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;
import java.util.Arrays;

public class BGHistoryLog extends HistoryLog {

    private byte[] first8Bytes;
    private int bg;
    private int cgmCalibration;
    private int bgSource;
    private float iob;
    private int targetBG;
    private int isf;
    private long spare;
    
    public BGHistoryLog() {}
    
    public BGHistoryLog(byte[] first8Bytes, int bg, int cgmCalibration, int bgSource, float iob, int targetBG, int isf, long spare) {
        this.cargo = buildCargo(first8Bytes, bg, cgmCalibration, bgSource, iob, targetBG, isf, spare);
        this.first8Bytes = first8Bytes;
        this.bg = bg;
        this.cgmCalibration = cgmCalibration;
        this.bgSource = bgSource;
        this.iob = iob;
        this.targetBG = targetBG;
        this.isf = isf;
        this.spare = spare;
        
    }

    public int typeId() {
        return 16;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        this.first8Bytes = Arrays.copyOfRange(raw, 2, 10);
        this.bg = Bytes.readShort(raw, 10);
        this.cgmCalibration = raw[12];
        this.bgSource = raw[13];
        this.iob = Bytes.readFloat(raw, 14);
        this.targetBG = Bytes.readShort(raw, 18);
        this.isf = Bytes.readShort(raw, 20);
        this.spare = Bytes.readUint32(raw, 22);
        
    }

    
    public static byte[] buildCargo(byte[] first8Bytes, int bg, int cgmCalibration, int bgSource, float iob, int targetBG, int isf, long spare) {
        return Bytes.combine(
            new byte[]{ 16, 0 },
            first8Bytes,
            Bytes.firstTwoBytesLittleEndian(bg), 
            new byte[]{ (byte) cgmCalibration }, 
            new byte[]{ (byte) bgSource }, 
            Bytes.toFloat(iob), 
            Bytes.firstTwoBytesLittleEndian(targetBG), 
            Bytes.firstTwoBytesLittleEndian(isf), 
            Bytes.toUint32(spare));
    }
    
    public int getBg() {
        return bg;
    }
    public int getCgmCalibration() {
        return cgmCalibration;
    }
    public int getBgSource() {
        return bgSource;
    }
    public float getIob() {
        return iob;
    }
    public int getTargetBG() {
        return targetBG;
    }
    public int getIsf() {
        return isf;
    }
    public long getSpare() {
        return spare;
    }
    
}