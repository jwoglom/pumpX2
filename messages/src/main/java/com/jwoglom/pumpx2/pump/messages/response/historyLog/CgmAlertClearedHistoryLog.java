package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;

@HistoryLogProps(
    opCode = 172,
    displayName = "CGM Alert Cleared",
    internalName = "LID_CGM_ALERT_CLEARED"
)
public class CgmAlertClearedHistoryLog extends HistoryLog {

    private long alertId;
    private CGMAlertStatusResponse.CGMAlert alert;

    public CgmAlertClearedHistoryLog() {}
    public CgmAlertClearedHistoryLog(long pumpTimeSec, long sequenceNum, long alertId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertId);
        this.alertId = alertId;
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId((int) alertId);

    }

    public CgmAlertClearedHistoryLog(long pumpTimeSec, long sequenceNum, CGMAlertStatusResponse.CGMAlert alert) {
        this(pumpTimeSec, sequenceNum, alert != null ? alert.id() : 0);
        this.alert = alert;

    }

    public CgmAlertClearedHistoryLog(long alertId) {
        this(0, 0, alertId);
    }

    public CgmAlertClearedHistoryLog(CGMAlertStatusResponse.CGMAlert alert) {
        this(0, 0, alert);
    }

    public int typeId() {
        return 172;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alertId = Bytes.readUint32(raw, 10);
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId((int) alertId);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long alertId) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 172, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(alertId)));
    }

    public long getAlertId() {
        return alertId;
    }

    public CGMAlertStatusResponse.CGMAlert getAlert() {
        return alert;
    }
}
