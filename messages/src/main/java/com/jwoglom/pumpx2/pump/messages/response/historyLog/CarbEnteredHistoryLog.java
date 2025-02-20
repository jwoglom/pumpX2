package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 48,
    displayName = "Carbs Entered",
    usedByTidepool = true
)
public class CarbEnteredHistoryLog extends HistoryLog {
    
    private float carbs;
    
    public CarbEnteredHistoryLog() {}
    public CarbEnteredHistoryLog(long pumpTimeSec, long sequenceNum, float carbs) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, carbs);
        this.carbs = carbs;
        
    }

    public CarbEnteredHistoryLog(float carbs) {
        this(0, 0, carbs);
    }

    public int typeId() {
        return 48;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.carbs = Bytes.readFloat(raw, 10);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float carbs) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{48, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(carbs)));
    }
    public float getCarbs() {
        return carbs;
    }
    
}