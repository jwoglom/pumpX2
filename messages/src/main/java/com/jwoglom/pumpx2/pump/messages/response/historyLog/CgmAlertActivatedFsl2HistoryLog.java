package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

/**
 * The payload layout mirrors {@link CgmAlertActivatedDexHistoryLog} (opcode 369), which has been
 * validated against captured records, and matches Tandem's cloud-export schema for this opcode.
 * No FreeStyle Libre 2 record has been captured yet, so the layout itself is unverified on the wire.
 */
@HistoryLogProps(
    opCode = 460,
    displayName = "CGM Alert Activated (FSL2)",
    internalName = "LID_CGM_ALERT_ACTIVATED_FSL2"
)
public class CgmAlertActivatedFsl2HistoryLog extends HistoryLog {

    private int alertId;
    private CGMAlertStatusResponse.CGMAlert alert;
    private int sensorType;
    private long faultLocatorData;
    private long param1;
    private float param2;

    public CgmAlertActivatedFsl2HistoryLog() {}
    public CgmAlertActivatedFsl2HistoryLog(long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long faultLocatorData, long param1, float param2) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertId, sensorType, faultLocatorData, param1, param2);
        this.alertId = alertId;
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId(alertId);
        this.sensorType = sensorType;
        this.faultLocatorData = faultLocatorData;
        this.param1 = param1;
        this.param2 = param2;

    }

    public CgmAlertActivatedFsl2HistoryLog(long pumpTimeSec, long sequenceNum, CGMAlertStatusResponse.CGMAlert alert, int sensorType, long faultLocatorData, long param1, float param2) {
        this(pumpTimeSec, sequenceNum, alert != null ? alert.id() : 0, sensorType, faultLocatorData, param1, param2);
        this.alert = alert;

    }

    public CgmAlertActivatedFsl2HistoryLog(int alertId, int sensorType, long faultLocatorData, long param1, float param2) {
        this(0, 0, alertId, sensorType, faultLocatorData, param1, param2);
    }

    public CgmAlertActivatedFsl2HistoryLog(CGMAlertStatusResponse.CGMAlert alert, int sensorType, long faultLocatorData, long param1, float param2) {
        this(0, 0, alert, sensorType, faultLocatorData, param1, param2);
    }

    public int typeId() {
        return 460;
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
            HistoryLog.typeIdBytes(460, 0),
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
     * The type of glucose sensor which raised the alert. Tandem's export schema maps 0 to Invalid
     * and 2 to CGM_TYPE_LIBRE2 for this opcode.
     */
    public int getSensorType() {
        return sensorType;
    }

    public CgmStatusV2Response.CgmSensorType getSensorTypeEnum() {
        return CgmStatusV2Response.CgmSensorType.fromId(sensorType);
    }

    /**
     * An opaque code identifying the firmware location which raised this alert. Field name and
     * position are taken from Tandem's cloud-export schema.
     */
    public long getFaultLocatorData() {
        return faultLocatorData;
    }

    /**
     * The measured value associated with the alert, as an unsigned 32-bit integer. By analogy with
     * {@link CgmAlertActivatedDexHistoryLog#getParam1()}; not observed for this opcode.
     */
    public long getParam1() {
        return param1;
    }

    /**
     * The threshold or configured setpoint associated with the alert. By analogy with
     * {@link CgmAlertActivatedDexHistoryLog#getParam2()}; not observed for this opcode.
     */
    public float getParam2() {
        return param2;
    }
}
