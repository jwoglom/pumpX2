package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 230, // -26
    displayName = "ControlIQ Pump Control Mode (PCM) Change",
    usedByTidepool = true // LID_AA_PCM_CHANGE
)
public class ControlIQPcmChangeHistoryLog extends HistoryLog {
    
    private int currentPcm;
    private int previousPcm;
    
    public ControlIQPcmChangeHistoryLog() {}
    public ControlIQPcmChangeHistoryLog(long pumpTimeSec, long sequenceNum, int currentPcm, int previousPcm) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentPcm, previousPcm);
        this.currentPcm = currentPcm;
        this.previousPcm = previousPcm;
        
    }

    public ControlIQPcmChangeHistoryLog(int currentPcm, int previousPcm) {
        this(0, 0, currentPcm, previousPcm);
    }

    public int typeId() {
        return 230;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.currentPcm = raw[10];
        this.previousPcm = raw[11];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int currentPcm, int previousPcm) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{-26, 0}, // (byte) 230
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) currentPcm }, 
            new byte[]{ (byte) previousPcm }));
    }
    public int getCurrentPcm() {
        return currentPcm;
    }
    public int getPreviousPcm() {
        return previousPcm;
    }
    
}