package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

public class TempRateActivatedHistoryLog extends HistoryLog {
    public static int ID = 2;
    
    
    public TempRateActivatedHistoryLog(long pumpTimeSec, long sequenceNum) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum);
        
    }

    public TempRateActivatedHistoryLog() {
        this(0, 0);
    }

    public int typeId() {
        return ID;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return Bytes.combine(
            new byte[]{(byte) ID, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum));
    }
    
}