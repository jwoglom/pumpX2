package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 5,
    displayName = "Alarm Activated",
    internalName = "LID_ALARM_ACTIVATED",
    usedByTidepool = true
)
public class AlarmActivatedHistoryLog extends HistoryLog {

    private long alarmId;
    private long faultLocatorData;
    private long param1;
    private float param2;

    public AlarmActivatedHistoryLog() {}
    public AlarmActivatedHistoryLog(long pumpTimeSec, long sequenceNum, long alarmId, long faultLocatorData, long param1, float param2) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alarmId, faultLocatorData, param1, param2);
        this.alarmId = alarmId;
        this.faultLocatorData = faultLocatorData;
        this.param1 = param1;
        this.param2 = param2;
    }

    public AlarmActivatedHistoryLog(long alarmId, long faultLocatorData, long param1, float param2) {
        this(0, 0, alarmId, faultLocatorData, param1, param2);
    }

    public AlarmActivatedHistoryLog(long pumpTimeSec, long sequenceNum, long alarmId) {
        this(pumpTimeSec, sequenceNum, alarmId, 0, 0, 0f);
    }

    public AlarmActivatedHistoryLog(long alarmId) {
        this(0, 0, alarmId);
    }

    public int typeId() {
        return 5;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alarmId = Bytes.readUint32(raw, 10);
        this.faultLocatorData = Bytes.readUint32(raw, 14);
        this.param1 = Bytes.readUint32(raw, 18);
        this.param2 = Bytes.readFloat(raw, 22);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long alarmId, long faultLocatorData, long param1, float param2) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(5, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(alarmId),
            Bytes.toUint32(faultLocatorData),
            Bytes.toUint32(param1),
            Bytes.toFloat(param2)));
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long alarmId) {
        return buildCargo(pumpTimeSec, sequenceNum, alarmId, 0, 0, 0f);
    }

    public long getAlarmId() {
        return alarmId;
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
     * @return the type of alarm
     */
    public AlarmStatusResponse.AlarmResponseType getAlarmResponseType() {
        return AlarmStatusResponse.AlarmResponseType.fromSingularId(alarmId);
    }
}