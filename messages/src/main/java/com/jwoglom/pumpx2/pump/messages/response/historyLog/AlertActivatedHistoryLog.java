package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 4,
    displayName = "Alert Activated",
    usedByTidepool = true
)
public class AlertActivatedHistoryLog extends HistoryLog {
    
    private long alertId;
    
    public AlertActivatedHistoryLog() {}
    public AlertActivatedHistoryLog(long pumpTimeSec, long sequenceNum, long alertId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertId);
        this.alertId = alertId;
        
    }

    public AlertActivatedHistoryLog(long alertId) {
        this(0, 0, alertId);
    }

    public int typeId() {
        return 4;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alertId = Bytes.readUint32(raw, 10);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long alertId) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{4, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(alertId)));
    }
    public long getAlertId() {
        return alertId;
    }
    
}