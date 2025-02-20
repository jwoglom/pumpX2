package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 199,
    displayName = "Hypo Minimizer Resume",
    usedByTidepool = true
)
public class HypoMinimizerResumeHistoryLog extends HistoryLog {
    
    private long reason;
    
    public HypoMinimizerResumeHistoryLog() {}
    public HypoMinimizerResumeHistoryLog(long pumpTimeSec, long sequenceNum, long reason) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, reason);
        this.reason = reason;
        
    }

    public HypoMinimizerResumeHistoryLog(long reason) {
        this(0, 0, reason);
    }

    public int typeId() {
        return 199;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.reason = Bytes.readUint32(raw, 10);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long reason) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{-57, 0}, // (byte) 199
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(reason)));
    }
    public long getReason() {
        return reason;
    }
    
}