package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.jwoglom.pumpx2.pump.messages.Message;

public abstract class ControlIQInfoAbstractResponse extends Message {
    abstract boolean getClosedLoopEnabled();
    abstract int getWeight();
    abstract int getWeightUnitId();
    abstract int getTotalDailyInsulin();
    abstract int getCurrentUserModeTypeId();
    abstract int getByte6();
    abstract int getByte7();
    abstract int getByte8();
    abstract int getControlStateType();

    public UserModeType getCurrentUserModeType() {
        return UserModeType.fromId(getCurrentUserModeTypeId());
    }

    public enum UserModeType {
        STANDARD(0),
        SLEEP(1),
        EXERCISE(2),
        ;

        private final int id;
        UserModeType(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static UserModeType fromId(int id) {
            for (UserModeType t : values()) {
                if (t.id == id) {
                    return t;
                }
            }
            return null;
        }
    }

    public WeightUnit getWeightUnit() {
        return WeightUnit.fromId(getWeightUnitId());
    }

    public enum WeightUnit {
        KILOGRAMS(0),
        POUNDS(1),
        ;

        private final int id;
        WeightUnit(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public static WeightUnit fromId(int id) {
            for (WeightUnit t: values()) {
                if (t.id == id) {
                    return t;
                }
            }
            return null;
        }
    }
}
