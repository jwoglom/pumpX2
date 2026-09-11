package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 4,
    displayName = "Alert Activated",
    internalName = "LID_ALERT_ACTIVATED",
    usedByTidepool = true
)
public class AlertActivatedHistoryLog extends HistoryLog {

    private long alertId;
    private long faultLocatorData;
    private long param1;
    private float param2;

    public AlertActivatedHistoryLog() {}
    public AlertActivatedHistoryLog(long pumpTimeSec, long sequenceNum, long alertId, long faultLocatorData, long param1, float param2) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alertId, faultLocatorData, param1, param2);
        this.alertId = alertId;
        this.faultLocatorData = faultLocatorData;
        this.param1 = param1;
        this.param2 = param2;
    }

    public AlertActivatedHistoryLog(long alertId, long faultLocatorData, long param1, float param2) {
        this(0, 0, alertId, faultLocatorData, param1, param2);
    }

    public AlertActivatedHistoryLog(long pumpTimeSec, long sequenceNum, long alertId) {
        this(pumpTimeSec, sequenceNum, alertId, 0, 0, 0f);
    }

    public AlertActivatedHistoryLog(long alertId) {
        this(0, 0, alertId);
    }

    public int typeId() {
        return 4;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alertId = Bytes.readUint32(raw, 10);
        this.faultLocatorData = Bytes.readUint32(raw, 14);
        this.param1 = Bytes.readUint32(raw, 18);
        this.param2 = Bytes.readFloat(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long alertId, long faultLocatorData, long param1, float param2) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(4, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(alertId),
            Bytes.toUint32(faultLocatorData),
            Bytes.toUint32(param1),
            Bytes.toFloat(param2)));
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long alertId) {
        return buildCargo(pumpTimeSec, sequenceNum, alertId, 0, 0, 0f);
    }

    public long getAlertId() {
        return alertId;
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

    /**
     * @return the type of alert
     */
    public AlertStatusResponse.AlertResponseType getAlertResponseType() {
        return AlertStatusResponse.AlertResponseType.fromSingularId(alertId);
    }

}
