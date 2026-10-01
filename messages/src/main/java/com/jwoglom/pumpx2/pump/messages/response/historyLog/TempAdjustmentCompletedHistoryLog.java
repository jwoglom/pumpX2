package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * Temporary adjustment (temp basal rate or temp control rate) completed.
 */
@HistoryLogProps(
    opCode = 564,
    displayName = "Temp Adjustment Completed",
    internalName = "LID_TEMP_ADJUSTMENT_COMPLETED"
)
public class TempAdjustmentCompletedHistoryLog extends HistoryLog {

    private int stopReason;
    private int tempRateId;
    private long timeLeft;

    public TempAdjustmentCompletedHistoryLog() {}
    public TempAdjustmentCompletedHistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0);
    }

    public TempAdjustmentCompletedHistoryLog(long pumpTimeSec, long sequenceNum, int stopReason, int tempRateId, long timeLeft) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, stopReason, tempRateId, timeLeft);
        parse(cargo);
    }

    public TempAdjustmentCompletedHistoryLog(int stopReason, int tempRateId, long timeLeft) {
        this(0, 0, stopReason, tempRateId, timeLeft);
    }

    public int typeId() {
        return 564;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.stopReason = Bytes.readShort(raw, 10);
        this.tempRateId = Bytes.readShort(raw, 12);
        this.timeLeft = Bytes.readUint32(raw, 14);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int stopReason, int tempRateId, long timeLeft) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(564, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(stopReason),
            Bytes.firstTwoBytesLittleEndian(tempRateId),
            Bytes.toUint32(timeLeft)));
    }

    /**
     * @return raw stop reason, see {@link StopReason}
     */
    public int getStopReason() {
        return stopReason;
    }
    public StopReason getStopReasonEnum() {
        return StopReason.fromId(stopReason);
    }
    /**
     * @return the temp rate ID
     */
    public int getTempRateId() {
        return tempRateId;
    }
    /**
     * @return time left, in milliseconds
     */
    public long getTimeLeft() {
        return timeLeft;
    }

    public enum StopReason {
        USER_ABORTED(0),
        TERMINATED_BY_ALARM(1),
        COMPLETED(3),
        USER_ABORTED_BLE(8),
        TERMINATED_BY_PUMP_CONTROL_MODE_CHANGE(10),
        TERMINATED_BY_CLOSED_LOOP_PREFERRED_CHANGE(11)

        ;
        private final int id;
        StopReason(int id) {
            this.id = id;
        }

        public static StopReason fromId(int id) {
            for (StopReason r : values()) {
                if (r.id == id) {
                    return r;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }
}
