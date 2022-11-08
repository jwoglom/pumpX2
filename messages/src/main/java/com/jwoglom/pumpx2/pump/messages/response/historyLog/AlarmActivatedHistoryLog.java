package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 5,
    displayName = "Alarm Activated",
    usedByTidepool = true
)
public class AlarmActivatedHistoryLog extends HistoryLog {
    
    private long alarmId;
    
    public AlarmActivatedHistoryLog() {}
    public AlarmActivatedHistoryLog(long pumpTimeSec, long sequenceNum, long alarmId) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, alarmId);
        this.alarmId = alarmId;
        
    }

    public AlarmActivatedHistoryLog(long alarmId) {
        this(0, 0, alarmId);
    }

    public int typeId() {
        return 5;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.alarmId = Bytes.readUint32(raw, 10);
        
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, long alarmId) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{5, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.toUint32(alarmId)));
    }
    public long getAlarmId() {
        return alarmId;
    }

    /**
     * @return the type of alarm
     */
    public AlarmStatusResponse.AlarmResponseType getAlarmResponseType() {
        return AlarmStatusResponse.AlarmResponseType.fromSingularId(alarmId);
    }
}