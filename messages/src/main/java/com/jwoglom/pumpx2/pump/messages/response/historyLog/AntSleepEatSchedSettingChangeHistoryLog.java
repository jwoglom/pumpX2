package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * AIDANET (AIQ) sleep/eat schedule setting change.
 */
@HistoryLogProps(
    opCode = 566,
    displayName = "AIDANET Sleep/Eat Schedule Change",
    internalName = "LID_ANT_SLEEP_EAT_SCHED_SETTING_CHANGE"
)
public class AntSleepEatSchedSettingChangeHistoryLog extends HistoryLog {

    private int sleepEatSegmentStartTime0;
    private int sleepEatSegmentStartTime1;
    private int sleepEatSegmentStartTime2;
    private int sleepEatSegmentStartTime3;
    private int sleepEatSegmentType0;
    private int sleepEatSegmentType1;
    private int sleepEatSegmentType2;
    private int sleepEatSegmentType3;

    public AntSleepEatSchedSettingChangeHistoryLog() {}
    public AntSleepEatSchedSettingChangeHistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public AntSleepEatSchedSettingChangeHistoryLog(long pumpTimeSec, long sequenceNum, int sleepEatSegmentStartTime0, int sleepEatSegmentStartTime1, int sleepEatSegmentStartTime2, int sleepEatSegmentStartTime3, int sleepEatSegmentType0, int sleepEatSegmentType1, int sleepEatSegmentType2, int sleepEatSegmentType3) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, sleepEatSegmentStartTime0, sleepEatSegmentStartTime1, sleepEatSegmentStartTime2, sleepEatSegmentStartTime3, sleepEatSegmentType0, sleepEatSegmentType1, sleepEatSegmentType2, sleepEatSegmentType3);
        parse(cargo);
    }

    public AntSleepEatSchedSettingChangeHistoryLog(int sleepEatSegmentStartTime0, int sleepEatSegmentStartTime1, int sleepEatSegmentStartTime2, int sleepEatSegmentStartTime3, int sleepEatSegmentType0, int sleepEatSegmentType1, int sleepEatSegmentType2, int sleepEatSegmentType3) {
        this(0, 0, sleepEatSegmentStartTime0, sleepEatSegmentStartTime1, sleepEatSegmentStartTime2, sleepEatSegmentStartTime3, sleepEatSegmentType0, sleepEatSegmentType1, sleepEatSegmentType2, sleepEatSegmentType3);
    }

    public int typeId() {
        return 566;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.sleepEatSegmentStartTime0 = Bytes.readShort(raw, 10);
        this.sleepEatSegmentStartTime1 = Bytes.readShort(raw, 12);
        this.sleepEatSegmentStartTime2 = Bytes.readShort(raw, 14);
        this.sleepEatSegmentStartTime3 = Bytes.readShort(raw, 16);
        this.sleepEatSegmentType0 = raw[18] & 0xFF;
        this.sleepEatSegmentType1 = raw[19] & 0xFF;
        this.sleepEatSegmentType2 = raw[20] & 0xFF;
        this.sleepEatSegmentType3 = raw[21] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int sleepEatSegmentStartTime0, int sleepEatSegmentStartTime1, int sleepEatSegmentStartTime2, int sleepEatSegmentStartTime3, int sleepEatSegmentType0, int sleepEatSegmentType1, int sleepEatSegmentType2, int sleepEatSegmentType3) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(566, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            Bytes.firstTwoBytesLittleEndian(sleepEatSegmentStartTime0),
            Bytes.firstTwoBytesLittleEndian(sleepEatSegmentStartTime1),
            Bytes.firstTwoBytesLittleEndian(sleepEatSegmentStartTime2),
            Bytes.firstTwoBytesLittleEndian(sleepEatSegmentStartTime3),
            new byte[]{(byte) sleepEatSegmentType0},
            new byte[]{(byte) sleepEatSegmentType1},
            new byte[]{(byte) sleepEatSegmentType2},
            new byte[]{(byte) sleepEatSegmentType3}));
    }

    /**
     * @return start of schedule segment 0, in minutes past midnight
     */
    public int getSleepEatSegmentStartTime0() {
        return sleepEatSegmentStartTime0;
    }
    /**
     * @return start of schedule segment 1, in minutes past midnight
     */
    public int getSleepEatSegmentStartTime1() {
        return sleepEatSegmentStartTime1;
    }
    /**
     * @return start of schedule segment 2, in minutes past midnight
     */
    public int getSleepEatSegmentStartTime2() {
        return sleepEatSegmentStartTime2;
    }
    /**
     * @return start of schedule segment 3, in minutes past midnight
     */
    public int getSleepEatSegmentStartTime3() {
        return sleepEatSegmentStartTime3;
    }
    /**
     * @return raw type of schedule segment 0, see {@link AntDailyStatus1HistoryLog.SleepEatSegmentType}
     */
    public int getSleepEatSegmentType0() {
        return sleepEatSegmentType0;
    }
    /**
     * @return raw type of schedule segment 1, see {@link AntDailyStatus1HistoryLog.SleepEatSegmentType}
     */
    public int getSleepEatSegmentType1() {
        return sleepEatSegmentType1;
    }
    /**
     * @return raw type of schedule segment 2, see {@link AntDailyStatus1HistoryLog.SleepEatSegmentType}
     */
    public int getSleepEatSegmentType2() {
        return sleepEatSegmentType2;
    }
    /**
     * @return raw type of schedule segment 3, see {@link AntDailyStatus1HistoryLog.SleepEatSegmentType}
     */
    public int getSleepEatSegmentType3() {
        return sleepEatSegmentType3;
    }
}
