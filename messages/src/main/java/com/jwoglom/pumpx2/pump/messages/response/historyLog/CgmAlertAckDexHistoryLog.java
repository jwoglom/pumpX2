package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

@HistoryLogProps(
    opCode = 371,
    displayName = "CGM Alert Ack B",
    internalName = "LID_CGM_ALERT_ACK_B"
)
public class CgmAlertAckDexHistoryLog extends HistoryLog {

    private int alertId;
    private CGMAlertStatusResponse.CGMAlert alert;
    private int sensorType;
    private long ackSource;

    public CgmAlertAckDexHistoryLog() {}
    public CgmAlertAckDexHistoryLog(long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long ackSource) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertId, sensorType, ackSource);
        this.alertId = alertId;
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId(alertId);
        this.sensorType = sensorType;
        this.ackSource = ackSource;

    }

    public CgmAlertAckDexHistoryLog(long pumpTimeSec, long sequenceNum, CGMAlertStatusResponse.CGMAlert alert, int sensorType, long ackSource) {
        this(pumpTimeSec, sequenceNum, alert != null ? alert.id() : 0, sensorType, ackSource);
        this.alert = alert;

    }

    public CgmAlertAckDexHistoryLog(int alertId, int sensorType, long ackSource) {
        this(0, 0, alertId, sensorType, ackSource);
    }

    public CgmAlertAckDexHistoryLog(CGMAlertStatusResponse.CGMAlert alert, int sensorType, long ackSource) {
        this(0, 0, alert, sensorType, ackSource);
    }

    public int typeId() {
        return 371;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alertId = raw[10];
        this.alert = CGMAlertStatusResponse.CGMAlert.fromId(alertId);
        this.sensorType = raw[11];
        this.ackSource = Bytes.readUint32(raw, 14);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long ackSource) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(371, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) alertId },
            new byte[]{ (byte) sensorType },
            new byte[2],
            Bytes.toUint32(ackSource)));
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

    /**
     * What acknowledged the alert, per Tandem's export schema: 0 = acknowledged by the user,
     * 1 = acknowledged by software (see {@link AckSource}). Both values occur in captured records.
     */
    public long getAckSource() {
        return ackSource;
    }

    public AckSource getAckSourceEnum() {
        return AckSource.fromId(ackSource);
    }

    public enum AckSource {
        USER(0),
        SOFTWARE(1),

        ;
        private final long id;
        AckSource(long id) {
            this.id = id;
        }

        public static AckSource fromId(long id) {
            for (AckSource a : values()) {
                if (a.id == id) {
                    return a;
                }
            }
            return null;
        }

        public long getId() {
            return id;
        }
    }
}
