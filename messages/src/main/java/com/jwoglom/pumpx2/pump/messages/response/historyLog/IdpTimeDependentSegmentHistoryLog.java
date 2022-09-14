package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 68,
    displayName = "Personal Profile (IDP) Time Dependent Segment",
    usedByTidepool = true
)
public class IdpTimeDependentSegmentHistoryLog extends HistoryLog {
    
    private int idp;
    private int status;
    private int segmentIndex;
    private int modificationType;
    private int startTime;
    private int basalRate;
    private int isf;
    private long targetBg;
    private int carbRatio;
    
    public IdpTimeDependentSegmentHistoryLog() {}
    public IdpTimeDependentSegmentHistoryLog(long pumpTimeSec, long sequenceNum, int idp, int status, int segmentIndex, int modificationType, int startTime, int basalRate, int isf, long targetBg, int carbRatio) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, idp, status, segmentIndex, modificationType, startTime, basalRate, isf, targetBg, carbRatio);
        this.idp = idp;
        this.status = status;
        this.segmentIndex = segmentIndex;
        this.modificationType = modificationType;
        this.startTime = startTime;
        this.basalRate = basalRate;
        this.isf = isf;
        this.targetBg = targetBg;
        this.carbRatio = carbRatio;
        
    }

    public IdpTimeDependentSegmentHistoryLog(int idp, int status, int segmentIndex, int modificationType, int startTime, int basalRate, int isf, long targetBg, int carbRatio) {
        this(0, 0, idp, status, segmentIndex, modificationType, startTime, basalRate, isf, targetBg, carbRatio);
    }

    public int typeId() {
        return 68;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.idp = raw[10];
        this.status = raw[11];
        this.segmentIndex = raw[12];
        this.modificationType = raw[13];
        this.startTime = Bytes.readShort(raw, 14);
        this.basalRate = Bytes.readShort(raw, 16);
        this.isf = Bytes.readShort(raw, 18);
        this.targetBg = Bytes.readUint32(raw, 20);
        this.carbRatio = raw[24];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int idp, int status, int segmentIndex, int modificationType, int startTime, int basalRate, int isf, long targetBg, int carbRatio) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{68, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) idp }, 
            new byte[]{ (byte) status }, 
            new byte[]{ (byte) segmentIndex }, 
            new byte[]{ (byte) modificationType }, 
            Bytes.firstTwoBytesLittleEndian(startTime), 
            Bytes.firstTwoBytesLittleEndian(basalRate), 
            Bytes.firstTwoBytesLittleEndian(isf), 
            Bytes.toUint32(targetBg), 
            new byte[]{ (byte) carbRatio }));
    }
    public int getIdp() {
        return idp;
    }
    public int getStatus() {
        return status;
    }
    public int getSegmentIndex() {
        return segmentIndex;
    }
    public int getModificationType() {
        return modificationType;
    }
    public int getStartTime() {
        return startTime;
    }
    public int getBasalRate() {
        return basalRate;
    }
    public int getIsf() {
        return isf;
    }
    public long getTargetBg() {
        return targetBg;
    }
    public int getCarbRatio() {
        return carbRatio;
    }
    
}