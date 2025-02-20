package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 151,
    displayName = "CGM Data Sample",
    usedByTidepool = true
)
public class CgmDataSampleHistoryLog extends HistoryLog {
    
    private int status;
    private int value;
    
    public CgmDataSampleHistoryLog() {}
    public CgmDataSampleHistoryLog(long pumpTimeSec, long sequenceNum, int status, int value) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, status, value);
        this.status = status;
        this.value = value;
        
    }

    public CgmDataSampleHistoryLog(int status, int value) {
        this(0, 0, status, value);
    }

    public int typeId() {
        return 151;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.status = Bytes.readShort(raw, 10);
        this.value = Bytes.readShort(raw, 19);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int status, int value) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{-105, 0}, // (byte) 151
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(status), 
            Bytes.firstTwoBytesLittleEndian(value)));
    }
    public int getStatus() {
        return status;
    }
    public int getValue() {
        return value;
    }
    
}