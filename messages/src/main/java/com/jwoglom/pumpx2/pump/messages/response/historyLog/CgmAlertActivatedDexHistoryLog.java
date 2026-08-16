package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;

@HistoryLogProps(
    opCode = 369,
    displayName = "CGM Alert Activated B",
    internalName = "LID_CGM_ALERT_ACTIVATED_B"
)
public class CgmAlertActivatedDexHistoryLog extends HistoryLog {

    private int alertId;
    private CGMAlertStatusResponse.CGMAlert alert;
    private int sensorType;
    private long faultLocatorData;
    private long param1;
    private float param2;

    public CgmAlertActivatedDexHistoryLog() {}
    public CgmAlertActivatedDexHistoryLog(long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long faultLocatorData, long param1, float param2) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertId, sensorType, faultLocatorData, param1, param2);
        this.alertId = alertId;
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId(alertId);
        this.sensorType = sensorType;
        this.faultLocatorData = faultLocatorData;
        this.param1 = param1;
        this.param2 = param2;

    }

    public CgmAlertActivatedDexHistoryLog(long pumpTimeSec, long sequenceNum, CGMAlertStatusResponse.CGMAlert alert, int sensorType, long faultLocatorData, long param1, float param2) {
        this(pumpTimeSec, sequenceNum, alert != null ? alert.id() : 0, sensorType, faultLocatorData, param1, param2);
        this.alert = alert;

    }

    public CgmAlertActivatedDexHistoryLog(int alertId, int sensorType, long faultLocatorData, long param1, float param2) {
        this(0, 0, alertId, sensorType, faultLocatorData, param1, param2);
    }

    public CgmAlertActivatedDexHistoryLog(CGMAlertStatusResponse.CGMAlert alert, int sensorType, long faultLocatorData, long param1, float param2) {
        this(0, 0, alert, sensorType, faultLocatorData, param1, param2);
    }

    public int typeId() {
        return 369;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alertId = raw[10];
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId(alertId);
        this.sensorType = raw[11];
        this.faultLocatorData = Bytes.readUint32(raw, 14);
        this.param1 = Bytes.readUint32(raw, 18);
        this.param2 = Bytes.readFloat(raw, 22);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long faultLocatorData, long param1, float param2) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(369, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) alertId },
            new byte[]{ (byte) sensorType },
            new byte[2],
            Bytes.toUint32(faultLocatorData),
            Bytes.toUint32(param1),
            Bytes.toFloat(param2)));
    }

    public int getAlertId() {
        return alertId;
    }

    public CGMAlertStatusResponse.CGMAlert getAlert() {
        return alert;
    }

    /**
     * The type of glucose sensor which raised the alert. Constant (3) in every captured record;
     * the meaning of other values is unconfirmed.
     */
    public int getSensorType() {
        return sensorType;
    }

    /**
     * An opaque code identifying the firmware location which raised this alert. Field name and
     * position are taken from tconnectsync's cloud-export schema; the values are stable per
     * alertId in the capture used to validate this field, but their internal structure is not
     * otherwise documented.
     */
    public long getFaultLocatorData() {
        return faultLocatorData;
    }

    /**
     * The measured value associated with the alert (e.g. the glucose reading, in mg/dL, that
     * triggered a threshold alert), as an unsigned 32-bit integer.
     */
    public long getParam1() {
        return param1;
    }

    /**
     * The threshold or configured setpoint associated with the alert (e.g. 80.0 or 200.0 mg/dL
     * for low/high glucose alerts).
     */
    public float getParam2() {
        return param2;
    }
}
