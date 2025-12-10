package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;

@HistoryLogProps(
    opCode = 28,
    displayName = "Alarm Cleared",
    internalName = "LID_ALARM_CLEARED"
)
public class AlarmClearedHistoryLog extends HistoryLog {

    private long alarmId;

    public AlarmClearedHistoryLog() {}
    public AlarmClearedHistoryLog(long pumpTimeSec, long sequenceNum, long alarmId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alarmId);
        this.alarmId = alarmId;

    }

    public AlarmClearedHistoryLog(long alarmId) {
        this(0, 0, alarmId);
    }

    public int typeId() {
        return 28;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alarmId = Bytes.readUint32(raw, 10);

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long alarmId) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{28, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(alarmId)));
    }

    public long getAlarmId() {
        return alarmId;
    }

    public AlarmStatusResponse.AlarmResponseType getAlarmResponseType() {
        return AlarmStatusResponse.AlarmResponseType.fromSingularId(alarmId);
    }
}
