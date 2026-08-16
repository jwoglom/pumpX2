package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;

@HistoryLogProps(
    opCode = 171,
    displayName = "CGM Alert Activated",
    internalName = "LID_CGM_ALERT_ACTIVATED"
)
public class CgmAlertActivatedHistoryLog extends HistoryLog {

    private long alertId;
    private CGMAlertStatusResponse.CGMAlert alert;
    private long faultLocatorData;
    private long param1;
    private float param2;

    public CgmAlertActivatedHistoryLog() {}
    public CgmAlertActivatedHistoryLog(long pumpTimeSec, long sequenceNum, long alertId, long faultLocatorData, long param1, float param2) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertId, faultLocatorData, param1, param2);
        this.alertId = alertId;
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId((int) alertId);
        this.faultLocatorData = faultLocatorData;
        this.param1 = param1;
        this.param2 = param2;

    }

    public CgmAlertActivatedHistoryLog(long pumpTimeSec, long sequenceNum, CGMAlertStatusResponse.CGMAlert alert, long faultLocatorData, long param1, float param2) {
        this(pumpTimeSec, sequenceNum, alert != null ? alert.id() : 0, faultLocatorData, param1, param2);
        this.alert = alert;

    }

    public CgmAlertActivatedHistoryLog(long alertId, long faultLocatorData, long param1, float param2) {
        this(0, 0, alertId, faultLocatorData, param1, param2);
    }

    public CgmAlertActivatedHistoryLog(CGMAlertStatusResponse.CGMAlert alert, long faultLocatorData, long param1, float param2) {
        this(0, 0, alert, faultLocatorData, param1, param2);
    }

    public int typeId() {
        return 171;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alertId = Bytes.readUint32(raw, 10);
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId((int) alertId);
        this.faultLocatorData = Bytes.readUint32(raw, 14);
        this.param1 = Bytes.readUint32(raw, 18);
        this.param2 = Bytes.readFloat(raw, 22);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long alertId, long faultLocatorData, long param1, float param2) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(171, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(alertId),
            Bytes.toUint32(faultLocatorData),
            Bytes.toUint32(param1),
            Bytes.toFloat(param2)));
    }

    public long getAlertId() {
        return alertId;
    }

    public CGMAlertStatusResponse.CGMAlert getAlert() {
        return alert;
    }

    public long getFaultLocatorData() {
        return faultLocatorData;
    }

    public long getParam1() {
        return param1;
    }

    public float getParam2() {
        return param2;
    }
}
