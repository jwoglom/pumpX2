package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 11,
    displayName = "Pumping Suspended",
    usedByTidepool = true
)
public class PumpingSuspendedHistoryLog extends HistoryLog {
    
    private int insulinAmount;
    private int reason;
    
    public PumpingSuspendedHistoryLog() {}
    public PumpingSuspendedHistoryLog(long pumpTimeSec, long sequenceNum, int insulinAmount, int reason) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, insulinAmount, reason);
        this.insulinAmount = insulinAmount;
        this.reason = reason;
        
    }

    public PumpingSuspendedHistoryLog(int insulinAmount, int reason) {
        this(0, 0, insulinAmount, reason);
    }

    public int typeId() {
        return 11;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.insulinAmount = Bytes.readShort(raw, 14);
        this.reason = raw[16];
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int insulinAmount, int reason) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{11, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(insulinAmount), 
            new byte[]{ (byte) reason }));
    }
    public int getInsulinAmount() {
        return insulinAmount;
    }
    public int getReason() {
        return reason;
    }
    
}