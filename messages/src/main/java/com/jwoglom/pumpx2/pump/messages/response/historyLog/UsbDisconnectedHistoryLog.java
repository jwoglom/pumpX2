package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 37,
    displayName = "USB Disconnected",
    usedByTidepool = true
)
public class UsbDisconnectedHistoryLog extends HistoryLog {
    
    private float negotiatedCurrentMilliAmps;
    
    public UsbDisconnectedHistoryLog() {}
    public UsbDisconnectedHistoryLog(long pumpTimeSec, long sequenceNum, float negotiatedCurrentMilliAmps) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, negotiatedCurrentMilliAmps);
        this.negotiatedCurrentMilliAmps = negotiatedCurrentMilliAmps;
        
    }

    public UsbDisconnectedHistoryLog(float negotiatedCurrentMilliAmps) {
        this(0, 0, negotiatedCurrentMilliAmps);
    }

    public int typeId() {
        return 37;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.negotiatedCurrentMilliAmps = Bytes.readFloat(raw, 10);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, float negotiatedCurrentMilliAmps) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{37, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toFloat(negotiatedCurrentMilliAmps)));
    }
    public float getNegotiatedCurrentMilliAmps() {
        return negotiatedCurrentMilliAmps;
    }
    
}