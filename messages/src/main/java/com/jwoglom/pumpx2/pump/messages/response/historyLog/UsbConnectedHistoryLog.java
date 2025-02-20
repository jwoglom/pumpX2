package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 36,
    displayName = "USB Connected",
    usedByTidepool = true
)
public class UsbConnectedHistoryLog extends HistoryLog {
    
    private float negotiatedCurrentmA;
    
    public UsbConnectedHistoryLog() {}
    public UsbConnectedHistoryLog(long pumpTimeSec, long sequenceNum, float negotiatedCurrentmA) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, negotiatedCurrentmA);
        this.negotiatedCurrentmA = negotiatedCurrentmA;
        
    }

    public UsbConnectedHistoryLog(float negotiatedCurrentmA) {
        this(0, 0, negotiatedCurrentmA);
    }

    public int typeId() {
        return 36;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.negotiatedCurrentmA = Bytes.readFloat(raw, 10);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float negotiatedCurrentmA) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{36, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(negotiatedCurrentmA)));
    }
    public float getNegotiatedCurrentmA() {
        return negotiatedCurrentmA;
    }
    
}