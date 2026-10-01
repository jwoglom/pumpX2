package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

/**
 * AIDANET (AIQ) new day record 1: algorithm settings and the sleep/eat schedule.
 */
@HistoryLogProps(
    opCode = 562,
    displayName = "AIDANET Daily Status 1",
    internalName = "LID_ANT_DAILY_STATUS1"
)
public class AntDailyStatus1HistoryLog extends HistoryLog {

    private boolean closedLoopPreferred;
    private int totalDailyInsulinUnits;
    private int glycemicGoal;
    private int priorTherapy;
    private int sleepEatSegmentStartTime0;
    private int sleepEatSegmentStartTime1;
    private int sleepEatSegmentStartTime2;
    private int sleepEatSegmentStartTime3;
    private int sleepEatSegmentType0;
    private int sleepEatSegmentType1;
    private int sleepEatSegmentType2;
    private int sleepEatSegmentType3;

    public AntDailyStatus1HistoryLog() {}
    public AntDailyStatus1HistoryLog(long pumpTimeSec, long sequenceNum) {
        this(pumpTimeSec, sequenceNum, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public AntDailyStatus1HistoryLog(long pumpTimeSec, long sequenceNum, boolean closedLoopPreferred, int totalDailyInsulinUnits, int glycemicGoal, int priorTherapy, int sleepEatSegmentStartTime0, int sleepEatSegmentStartTime1, int sleepEatSegmentStartTime2, int sleepEatSegmentStartTime3, int sleepEatSegmentType0, int sleepEatSegmentType1, int sleepEatSegmentType2, int sleepEatSegmentType3) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, closedLoopPreferred, totalDailyInsulinUnits, glycemicGoal, priorTherapy, sleepEatSegmentStartTime0, sleepEatSegmentStartTime1, sleepEatSegmentStartTime2, sleepEatSegmentStartTime3, sleepEatSegmentType0, sleepEatSegmentType1, sleepEatSegmentType2, sleepEatSegmentType3);
        parse(cargo);
    }

    public AntDailyStatus1HistoryLog(boolean closedLoopPreferred, int totalDailyInsulinUnits, int glycemicGoal, int priorTherapy, int sleepEatSegmentStartTime0, int sleepEatSegmentStartTime1, int sleepEatSegmentStartTime2, int sleepEatSegmentStartTime3, int sleepEatSegmentType0, int sleepEatSegmentType1, int sleepEatSegmentType2, int sleepEatSegmentType3) {
        this(0, 0, closedLoopPreferred, totalDailyInsulinUnits, glycemicGoal, priorTherapy, sleepEatSegmentStartTime0, sleepEatSegmentStartTime1, sleepEatSegmentStartTime2, sleepEatSegmentStartTime3, sleepEatSegmentType0, sleepEatSegmentType1, sleepEatSegmentType2, sleepEatSegmentType3);
    }

    public int typeId() {
        return 562;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.closedLoopPreferred = raw[10] != 0;
        this.totalDailyInsulinUnits = raw[11] & 0xFF;
        this.glycemicGoal = raw[12] & 0xFF;
        this.priorTherapy = raw[13] & 0xFF;
        this.sleepEatSegmentStartTime0 = Bytes.readShort(raw, 14);
        this.sleepEatSegmentStartTime1 = Bytes.readShort(raw, 16);
        this.sleepEatSegmentStartTime2 = Bytes.readShort(raw, 18);
        this.sleepEatSegmentStartTime3 = Bytes.readShort(raw, 20);
        this.sleepEatSegmentType0 = raw[22] & 0xFF;
        this.sleepEatSegmentType1 = raw[23] & 0xFF;
        this.sleepEatSegmentType2 = raw[24] & 0xFF;
        this.sleepEatSegmentType3 = raw[25] & 0xFF;
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum) {
        return buildCargo(pumpTimeSec, sequenceNum, false, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, boolean closedLoopPreferred, int totalDailyInsulinUnits, int glycemicGoal, int priorTherapy, int sleepEatSegmentStartTime0, int sleepEatSegmentStartTime1, int sleepEatSegmentStartTime2, int sleepEatSegmentStartTime3, int sleepEatSegmentType0, int sleepEatSegmentType1, int sleepEatSegmentType2, int sleepEatSegmentType3) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(562, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{(byte) (closedLoopPreferred ? 1 : 0)},
            new byte[]{(byte) totalDailyInsulinUnits},
            new byte[]{(byte) glycemicGoal},
            new byte[]{(byte) priorTherapy},
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
     * @return whether closed loop is preferred
     */
    public boolean isClosedLoopPreferred() {
        return closedLoopPreferred;
    }
    /**
     * @return total daily insulin setting, in units
     */
    public int getTotalDailyInsulinUnits() {
        return totalDailyInsulinUnits;
    }
    /**
     * @return raw glycemic goal, see {@link GlycemicGoal}
     */
    public int getGlycemicGoal() {
        return glycemicGoal;
    }
    public GlycemicGoal getGlycemicGoalEnum() {
        return GlycemicGoal.fromId(glycemicGoal);
    }
    /**
     * @return raw prior therapy, see {@link PriorTherapy}
     */
    public int getPriorTherapy() {
        return priorTherapy;
    }
    public PriorTherapy getPriorTherapyEnum() {
        return PriorTherapy.fromId(priorTherapy);
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
     * @return raw type of schedule segment 0, see {@link SleepEatSegmentType}
     */
    public int getSleepEatSegmentType0() {
        return sleepEatSegmentType0;
    }
    public SleepEatSegmentType getSleepEatSegmentType0Enum() {
        return SleepEatSegmentType.fromId(sleepEatSegmentType0);
    }
    /**
     * @return raw type of schedule segment 1, see {@link SleepEatSegmentType}
     */
    public int getSleepEatSegmentType1() {
        return sleepEatSegmentType1;
    }
    public SleepEatSegmentType getSleepEatSegmentType1Enum() {
        return SleepEatSegmentType.fromId(sleepEatSegmentType1);
    }
    /**
     * @return raw type of schedule segment 2, see {@link SleepEatSegmentType}
     */
    public int getSleepEatSegmentType2() {
        return sleepEatSegmentType2;
    }
    public SleepEatSegmentType getSleepEatSegmentType2Enum() {
        return SleepEatSegmentType.fromId(sleepEatSegmentType2);
    }
    /**
     * @return raw type of schedule segment 3, see {@link SleepEatSegmentType}
     */
    public int getSleepEatSegmentType3() {
        return sleepEatSegmentType3;
    }
    public SleepEatSegmentType getSleepEatSegmentType3Enum() {
        return SleepEatSegmentType.fromId(sleepEatSegmentType3);
    }

    public enum GlycemicGoal {
        RELAXED(0),
        BALANCED(1),
        TIGHTEST(2)

        ;
        private final int id;
        GlycemicGoal(int id) {
            this.id = id;
        }

        public static GlycemicGoal fromId(int id) {
            for (GlycemicGoal r : values()) {
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

    public enum PriorTherapy {
        PREVIOUS_PUMPING_EXPERIENCE(0),
        MULTIPLE_DAILY_INJECTIONS(1),
        NOT_SET(2)

        ;
        private final int id;
        PriorTherapy(int id) {
            this.id = id;
        }

        public static PriorTherapy fromId(int id) {
            for (PriorTherapy r : values()) {
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

    public enum SleepEatSegmentType {
        NONE(0),
        LESS(1),
        STANDARD(2),
        MORE(3),
        MAX(4),
        SLEEP(5),
        NOT_DEFINED(6)

        ;
        private final int id;
        SleepEatSegmentType(int id) {
            this.id = id;
        }

        public static SleepEatSegmentType fromId(int id) {
            for (SleepEatSegmentType r : values()) {
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
