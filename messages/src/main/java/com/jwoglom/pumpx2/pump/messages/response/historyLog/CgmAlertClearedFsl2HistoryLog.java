package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

/**
 * The payload layout mirrors {@link CgmAlertClearedDexHistoryLog} (opcode 370), which has been
 * validated against captured records, and matches Tandem's cloud-export schema for this opcode.
 * No FreeStyle Libre 2 record has been captured yet, so the layout itself is unverified on the wire.
 */
@HistoryLogProps(
    opCode = 461,
    displayName = "CGM Alert Cleared (FSL2)",
    internalName = "LID_CGM_ALERT_CLEARED_FSL2"
)
public class CgmAlertClearedFsl2HistoryLog extends HistoryLog {

    private int alertId;
    private CGMAlertStatusResponse.CGMAlert alert;
    private int sensorType;

    public CgmAlertClearedFsl2HistoryLog() {}
    public CgmAlertClearedFsl2HistoryLog(long pumpTimeSec, long sequenceNum, int alertId, int sensorType) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertId, sensorType);
        this.alertId = alertId;
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId(alertId);
        this.sensorType = sensorType;

    }

    public CgmAlertClearedFsl2HistoryLog(long pumpTimeSec, long sequenceNum, CGMAlertStatusResponse.CGMAlert alert, int sensorType) {
        this(pumpTimeSec, sequenceNum, alert != null ? alert.id() : 0, sensorType);
        this.alert = alert;

    }

    public CgmAlertClearedFsl2HistoryLog(int alertId, int sensorType) {
        this(0, 0, alertId, sensorType);
    }

    public CgmAlertClearedFsl2HistoryLog(CGMAlertStatusResponse.CGMAlert alert, int sensorType) {
        this(0, 0, alert, sensorType);
    }

    public int typeId() {
        return 461;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alertId = raw[10];
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId(alertId);
        this.sensorType = raw[11];

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int alertId, int sensorType) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(461, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) alertId },
            new byte[]{ (byte) sensorType }));
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
}
