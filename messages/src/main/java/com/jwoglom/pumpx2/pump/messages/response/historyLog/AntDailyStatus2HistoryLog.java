package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * AIDANET (AIQ) new day record 2: pump control state and sensor type.
 */
@HistoryLogProps(
    opCode = 563,
    displayName = "AIDANET Daily Status 2",
    internalName = "LID_ANT_DAILY_STATUS2"
)
public class AntDailyStatus2HistoryLog extends HistoryLog {

    private int pumpControlState;
    private int sensorType;

    public AntDailyStatus2HistoryLog() {}
    public AntDailyStatus2HistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0);
    }

    public AntDailyStatus2HistoryLog(long pumpTimeSec, long sequenceNum, int pumpControlState, int sensorType) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, pumpControlState, sensorType);
        parse(cargo);
    }

    public AntDailyStatus2HistoryLog(int pumpControlState, int sensorType) {
        this(0, 0, pumpControlState, sensorType);
    }

    public int typeId() {
        return 563;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.pumpControlState = raw[10] & 0xFF;
        this.sensorType = raw[11] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int pumpControlState, int sensorType) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(563, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{(byte) pumpControlState},
            new byte[]{(byte) sensorType}));
    }

    /**
     * @return raw pump control state, see {@link DailyStatusHistoryLog.PumpControlState}
     */
    public int getPumpControlState() {
        return pumpControlState;
    }
    /**
     * @return raw sensor type, see {@link DailyStatusHistoryLog.SensorType}
     */
    public int getSensorType() {
        return sensorType;
    }
}
