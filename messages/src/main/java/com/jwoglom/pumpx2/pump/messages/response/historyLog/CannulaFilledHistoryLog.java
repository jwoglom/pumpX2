package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 61,
    displayName = "Cannula Filled",
    internalName = "LID_CANNULA_FILLED",
    usedByTidepool = true
)
public class CannulaFilledHistoryLog extends HistoryLog {
    
    private float primeSize;
    
    public CannulaFilledHistoryLog() {}
    public CannulaFilledHistoryLog(long pumpTimeSec, long sequenceNum, float primeSize) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, primeSize);
        this.primeSize = primeSize;
        
    }

    public CannulaFilledHistoryLog(float primeSize) {
        this(0, 0, primeSize);
    }

    public int typeId() {
        return 61;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.primeSize = Bytes.readFloat(raw, 10);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float primeSize) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{61, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(primeSize)));
    }

    /**
     * @return the number of units used to prime the cannula
     */
    public float getPrimeSize() {
        return primeSize;
    }
    
}