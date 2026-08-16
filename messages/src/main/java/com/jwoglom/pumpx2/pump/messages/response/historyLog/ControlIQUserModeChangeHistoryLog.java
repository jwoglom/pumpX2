package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.annotations.HistoryLogProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import java.math.BigInteger;

@HistoryLogProps(
    opCode = 229, // -27
    displayName = "ControlIQ User Mode Change",
    internalName = "LID_AA_USER_MODE_CHANGE",
    usedByTidepool = true // LID_AA_USER_MODE_CHANGE
)
public class ControlIQUserModeChangeHistoryLog extends HistoryLog {

    private int currentUserMode;
    private int previousUserMode;
    private int requestedActionRaw;
    private int sleepStartedByGuiRaw;
    private int activeSleepSchedule;
    private int exerciseStoppedByTimerRaw;
    private int exerciseChoiceRaw;
    private int exerciseTime;
    private int eatingSoonStoppedByTimerRaw;

    public ControlIQUserModeChangeHistoryLog() {}
    public ControlIQUserModeChangeHistoryLog(long pumpTimeSec, long sequenceNum, int currentUserMode, int previousUserMode, int requestedAction, int sleepStartedByGui, int activeSleepSchedule, int exerciseStoppedByTimer, int exerciseChoice, int exerciseTime, int eatingSoonStoppedByTimer) {
        super(pumpTimeSec, sequenceNum);
        this.cargo = buildCargo(pumpTimeSec, sequenceNum, currentUserMode, previousUserMode, requestedAction, sleepStartedByGui, activeSleepSchedule, exerciseStoppedByTimer, exerciseChoice, exerciseTime, eatingSoonStoppedByTimer);
        this.currentUserMode = currentUserMode;
        this.previousUserMode = previousUserMode;
        this.requestedActionRaw = requestedAction;
        this.sleepStartedByGuiRaw = sleepStartedByGui;
        this.activeSleepSchedule = activeSleepSchedule;
        this.exerciseStoppedByTimerRaw = exerciseStoppedByTimer;
        this.exerciseChoiceRaw = exerciseChoice;
        this.exerciseTime = exerciseTime;
        this.eatingSoonStoppedByTimerRaw = eatingSoonStoppedByTimer;

    }

    public ControlIQUserModeChangeHistoryLog(int currentUserMode, int previousUserMode, int requestedAction, int sleepStartedByGui, int activeSleepSchedule, int exerciseStoppedByTimer, int exerciseChoice, int exerciseTime, int eatingSoonStoppedByTimer) {
        this(0, 0, currentUserMode, previousUserMode, requestedAction, sleepStartedByGui, activeSleepSchedule, exerciseStoppedByTimer, exerciseChoice, exerciseTime, eatingSoonStoppedByTimer);
    }

    public int typeId() {
        return 229;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == 26);
        this.cargo = raw;
        parseBase(raw);
        this.currentUserMode = raw[10];
        this.previousUserMode = raw[11];
        this.requestedActionRaw = raw[12];
        this.sleepStartedByGuiRaw = raw[14];
        this.activeSleepSchedule = raw[15];
        this.exerciseStoppedByTimerRaw = raw[18];
        this.exerciseChoiceRaw = raw[19];
        this.exerciseTime = Bytes.readShort(raw, 20);
        this.eatingSoonStoppedByTimerRaw = raw[22];

    }

    public static byte[] buildCargo(long pumpTimeSec, long sequenceNum, int currentUserMode, int previousUserMode, int requestedAction, int sleepStartedByGui, int activeSleepSchedule, int exerciseStoppedByTimer, int exerciseChoice, int exerciseTime, int eatingSoonStoppedByTimer) {
        return HistoryLog.fillCargo(Bytes.combine(
            HistoryLog.typeIdBytes(229, 0),
            Bytes.toUint32(pumpTimeSec),
            Bytes.toUint32(sequenceNum),
            new byte[]{ (byte) currentUserMode },
            new byte[]{ (byte) previousUserMode },
            new byte[]{ (byte) requestedAction },
            new byte[1],
            new byte[]{ (byte) sleepStartedByGui },
            new byte[]{ (byte) activeSleepSchedule },
            new byte[2],
            new byte[]{ (byte) exerciseStoppedByTimer },
            new byte[]{ (byte) exerciseChoice },
            Bytes.firstTwoBytesLittleEndian(exerciseTime),
            new byte[]{ (byte) eatingSoonStoppedByTimer }));
    }
    public int getCurrentUserMode() {
        return currentUserMode;
    }
    public int getPreviousUserMode() {
        return previousUserMode;
    }

    public enum UserMode {
        NORMAL(0),
        SLEEPING(1),
        EXERCISING(2),
        EATING_SOON(3),

        ;

        private final int id;
        UserMode(int id) {
            this.id = id;
        }

        public static UserMode fromId(int id) {
            for (UserMode s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

    public UserMode getCurrentUserModeEnum() {
        return UserMode.fromId(currentUserMode);
    }

    public UserMode getPreviousUserModeEnum() {
        return UserMode.fromId(previousUserMode);
    }

    public enum RequestedAction {
        NO_USER_REQUEST(0),
        START_SLEEP(1),
        STOP_SLEEP(2),
        START_EXERCISE(3),
        STOP_EXERCISE(4),
        STOP_ALL(5),
        START_EATING_SOON(6),
        STOP_EATING_SOON(7),

        ;

        private final int id;
        RequestedAction(int id) {
            this.id = id;
        }

        public static RequestedAction fromId(int id) {
            for (RequestedAction s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

    public int getRequestedActionRaw() {
        return requestedActionRaw;
    }

    public RequestedAction getRequestedAction() {
        return RequestedAction.fromId(requestedActionRaw);
    }

    public int getSleepStartedByGuiRaw() {
        return sleepStartedByGuiRaw;
    }

    /**
     * True if the currently-active sleep activity was started manually via the pump GUI,
     * rather than by a scheduled sleep-activity time.
     */
    public boolean isSleepStartedByGui() {
        return sleepStartedByGuiRaw != 0;
    }

    /**
     * Bitmask where bits 0-3 indicate whether sleep schedules 1-4, respectively, are currently
     * active. Kept as a raw int since the individual bit semantics are not exercised by the
     * capture used to validate this field.
     */
    public int getActiveSleepSchedule() {
        return activeSleepSchedule;
    }

    public int getExerciseStoppedByTimerRaw() {
        return exerciseStoppedByTimerRaw;
    }

    /**
     * True if the currently-active (or just-ended) exercise activity was stopped automatically
     * by its configured timer, rather than manually via the pump GUI.
     */
    public boolean isExerciseStoppedByTimer() {
        return exerciseStoppedByTimerRaw != 0;
    }

    public enum ExerciseChoice {
        CONTINUOUS(0),
        TIMED(1),

        ;

        private final int id;
        ExerciseChoice(int id) {
            this.id = id;
        }

        public static ExerciseChoice fromId(int id) {
            for (ExerciseChoice s : values()) {
                if (s.id == id) {
                    return s;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }
    }

    public int getExerciseChoiceRaw() {
        return exerciseChoiceRaw;
    }

    public ExerciseChoice getExerciseChoice() {
        return ExerciseChoice.fromId(exerciseChoiceRaw);
    }

    /**
     * Duration, in minutes, of a timed exercise activity.
     */
    public int getExerciseTime() {
        return exerciseTime;
    }

    public int getEatingSoonStoppedByTimerRaw() {
        return eatingSoonStoppedByTimerRaw;
    }

    /**
     * True if the currently-active (or just-ended) eating-soon activity was stopped
     * automatically by its configured timer, rather than manually via the pump GUI.
     */
    public boolean isEatingSoonStoppedByTimer() {
        return eatingSoonStoppedByTimerRaw != 0;
    }

}
