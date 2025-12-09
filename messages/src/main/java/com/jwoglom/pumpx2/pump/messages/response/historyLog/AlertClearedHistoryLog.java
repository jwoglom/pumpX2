package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;

@HistoryLogProps(
    opCode = 26,
    displayName = "Alert Cleared",
    internalName = "LID_ALERT_CLEARED"
)
public class AlertClearedHistoryLog extends HistoryLog {

    private long alertId;
    private long faultLocatorData;

    public AlertClearedHistoryLog() {}
    public AlertClearedHistoryLog(long pumpTimeSec, long sequenceNum, long alertId, long faultLocatorData) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertId, faultLocatorData);
        this.alertId = alertId;
        this.faultLocatorData = faultLocatorData;

    }

    public AlertClearedHistoryLog(long alertId, long faultLocatorData) {
        this(0, 0, alertId, faultLocatorData);
    }

    public int typeId() {
        return 26;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alertId = Bytes.readUint32(raw, 10);
        this.faultLocatorData = Bytes.readUint32(raw, 14);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long alertId, long faultLocatorData) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{26, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(alertId),
            Bytes.toUint32(faultLocatorData)));
    }

    public long getAlertId() {
        return alertId;
    }

    public long getFaultLocatorData() {
        return faultLocatorData;
    }

    public AlertStatusResponse.AlertResponseType getAlertResponseType() {
        return AlertStatusResponse.AlertResponseType.fromSingularId(alertId);
    }
}
