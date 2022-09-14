package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 67,
    displayName = "USB Enumerated",
    usedByTidepool = true
)
public class UsbEnumeratedHistoryLog extends HistoryLog {
    
    private int negotiatedCurrentMilliAmps;
    
    public UsbEnumeratedHistoryLog() {}
    public UsbEnumeratedHistoryLog(long pumpTimeSec, long sequenceNum, int negotiatedCurrentMilliAmps) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, negotiatedCurrentMilliAmps);
        this.negotiatedCurrentMilliAmps = negotiatedCurrentMilliAmps;
        
    }

    public UsbEnumeratedHistoryLog(int negotiatedCurrentMilliAmps) {
        this(0, 0, negotiatedCurrentMilliAmps);
    }

    public int typeId() {
        return 67;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.negotiatedCurrentMilliAmps = raw[10];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int negotiatedCurrentMilliAmps) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{67, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) negotiatedCurrentMilliAmps }));
    }
    public int getNegotiatedCurrentMilliAmps() {
        return negotiatedCurrentMilliAmps;
    }
    
}