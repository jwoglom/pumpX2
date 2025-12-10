package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

@HistoryLogProps(
    opCode = 313,
    displayName = "Daily Status",
    internalName = "LID_AA_DAILY_STATUS"
)
public class DailyStatusHistoryLog extends HistoryLog {

    private int sensorType;
    private int userMode;
    private int pumpControlState;

    public DailyStatusHistoryLog() {}
    public DailyStatusHistoryLog(long pumpTimeSec, long sequenceNum, int sensorType, int userMode, int pumpControlState) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, sensorType, userMode, pumpControlState);
        this.sensorType = sensorType;
        this.userMode = userMode;
        this.pumpControlState = pumpControlState;

    }

    public DailyStatusHistoryLog(int sensorType, int userMode, int pumpControlState) {
        this(0, 0, sensorType, userMode, pumpControlState);
    }

    public int typeId() {
        return 313;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.sensorType = raw[11];
        this.userMode = raw[12];
        this.pumpControlState = raw[13];

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int sensorType, int userMode, int pumpControlState) {
        return HistoryLog.fillCargo(Bytes.combine(
            new byte[]{(byte) 313, 0},
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{0, (byte) sensorType, (byte) userMode, (byte) pumpControlState}));
    }

    public int getSensorType() {
        return sensorType;
    }

    public int getUserMode() {
        return userMode;
    }

    public int getPumpControlState() {
        return pumpControlState;
    }
}
