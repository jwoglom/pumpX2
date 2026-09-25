package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

@HistoryLogProps(
    opCode = 370,
    displayName = "CGM Alert Cleared B",
    internalName = "LID_CGM_ALERT_CLEARED_B"
)
public class CgmAlertClearedDexHistoryLog extends HistoryLog {

    private int alertId;
    private CGMAlertStatusResponse.CGMAlert alert;
    private int sensorType;

    public CgmAlertClearedDexHistoryLog() {}
    public CgmAlertClearedDexHistoryLog(long pumpTimeSec, long sequenceNum, int alertId, int sensorType) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertId, sensorType);
        this.alertId = alertId;
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId(alertId);
        this.sensorType = sensorType;

    }

    public CgmAlertClearedDexHistoryLog(long pumpTimeSec, long sequenceNum, CGMAlertStatusResponse.CGMAlert alert, int sensorType) {
        this(pumpTimeSec, sequenceNum, alert != null ? alert.id() : 0, sensorType);
        this.alert = alert;

    }

    public CgmAlertClearedDexHistoryLog(int alertId, int sensorType) {
        this(0, 0, alertId, sensorType);
    }

    public CgmAlertClearedDexHistoryLog(CGMAlertStatusResponse.CGMAlert alert, int sensorType) {
        this(0, 0, alert, sensorType);
    }

    public int typeId() {
        return 370;
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
            HistoryLog.typeIdBytes(370, 0),
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
     * The type of glucose sensor which raised the alert, as a CGMSensorType ordinal (see
     * {@link #getSensorTypeEnum()}). Tandem's export schema lists 0 = Invalid, 1 = Dexcom G6 and
     * 3 = Dexcom G7 for this opcode. Every captured record has 3, all from Dexcom G7 sessions.
     */
    public int getSensorType() {
        return sensorType;
    }

    public CgmStatusV2Response.CgmSensorType getSensorTypeEnum() {
        return CgmStatusV2Response.CgmSensorType.fromId(sensorType);
    }
}
