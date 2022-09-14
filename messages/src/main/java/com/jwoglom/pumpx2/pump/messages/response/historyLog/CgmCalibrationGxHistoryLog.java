package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 210,
    displayName = "CGM Calibration GX",
    usedByTidepool = true
)
public class CgmCalibrationGxHistoryLog extends HistoryLog {
    
    private int value;
    
    public CgmCalibrationGxHistoryLog() {}
    public CgmCalibrationGxHistoryLog(long pumpTimeSec, long sequenceNum, int value) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, value);
        this.value = value;
        
    }

    public CgmCalibrationGxHistoryLog(int value) {
        this(0, 0, value);
    }

    public int typeId() {
        return 210;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.value = Bytes.readShort(raw, 10);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int value) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{-46, 0}, // (byte) 210
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(value)));
    }
    public int getValue() {
        return value;
    }
    
}