package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 229, // -27
    displayName = "ControlIQ User Mode Change",
    internalName = "LID_AA_USER_MODE_CHANGE",
    usedByTidepool = true // LID_AA_USER_MODE_CHANGE
)
public class ControlIQUserModeChangeHistoryLog extends HistoryLog {
    
    private int currentUserMode;
    private int previousUserMode;
    
    public ControlIQUserModeChangeHistoryLog() {}
    public ControlIQUserModeChangeHistoryLog(long pumpTimeSec, long sequenceNum, int currentUserMode, int previousUserMode) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentUserMode, previousUserMode);
        this.currentUserMode = currentUserMode;
        this.previousUserMode = previousUserMode;
        
    }

    public ControlIQUserModeChangeHistoryLog(int currentUserMode, int previousUserMode) {
        this(0, 0, currentUserMode, previousUserMode);
    }

    public int typeId() {
        return 229;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.currentUserMode = raw[10];
        this.previousUserMode = raw[11];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int currentUserMode, int previousUserMode) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{-27, 0}, // (byte) 229
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) currentUserMode }, 
            new byte[]{ (byte) previousUserMode }));
    }
    public int getCurrentUserMode() {
        return currentUserMode;
    }
    public int getPreviousUserMode() {
        return previousUserMode;
    }
    
}