package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 90,
    displayName = "New Day",
    usedByTidepool = true
)
public class NewDayHistoryLog extends HistoryLog {
    
    private float commandedBasalRate;
    
    public NewDayHistoryLog() {}
    public NewDayHistoryLog(long pumpTimeSec, long sequenceNum, float commandedBasalRate) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, commandedBasalRate);
        this.commandedBasalRate = commandedBasalRate;
        
    }

    public NewDayHistoryLog(float commandedBasalRate) {
        this(0, 0, commandedBasalRate);
    }

    public int typeId() {
        return 90;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.commandedBasalRate = Bytes.readFloat(raw, 10);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float commandedBasalRate) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{90, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(commandedBasalRate)));
    }
    public float getCommandedBasalRate() {
        return commandedBasalRate;
    }
    
}