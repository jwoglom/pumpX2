package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 12,
    displayName = "Pumping Resumed",
    usedByTidepool = true
)
public class PumpingResumedHistoryLog extends HistoryLog {
    
    private int insulinAmount;
    
    public PumpingResumedHistoryLog() {}
    public PumpingResumedHistoryLog(long pumpTimeSec, long sequenceNum, int insulinAmount) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, insulinAmount);
        this.insulinAmount = insulinAmount;
        
    }

    public PumpingResumedHistoryLog(int insulinAmount) {
        this(0, 0, insulinAmount);
    }

    public int typeId() {
        return 12;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.insulinAmount = Bytes.readShort(raw, 14);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int insulinAmount) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{12, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(insulinAmount)));
    }

    /**
     * @return the insulin amount at the time pumping was resumed (most likely the IOB, needs confirmation)
     */
    public int getInsulinAmount() {
        return insulinAmount;
    }
    
}