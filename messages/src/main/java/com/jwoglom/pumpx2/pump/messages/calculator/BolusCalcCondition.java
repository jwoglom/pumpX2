package com.jwoglom.pumpx2.pump.messages.calculator;

import java.util.Objects;

public interface BolusCalcCondition {
    public static final BolusCalcCondition POSITIVE_BG_CORRECTION = new Decision("Adding positive BG correction", "above BG target");
    public static final BolusCalcCondition NO_POSITIVE_BG_CORRECTION = new NonActionDecision("Adding positive BG correction", "active IOB is greater than correction bolus while above target");
    public static final BolusCalcCondition SET_ZERO_INSULIN = new NonActionDecision("Setting zero insulin", "negative correction greater than carb amount");
    public static final BolusCalcCondition NEGATIVE_BG_CORRECTION = new Decision("Adding negative BG correction", "below BG target");

    class FailedPrecondition extends Condition implements BolusCalcCondition {
        public final String reason;
        FailedPrecondition(String reason) {
            super(reason);
            this.reason = reason;
        }

        FailedPrecondition(String reason, Object was, Object expected) {
            this(reason + ": was " + was + ", expected " + expected);
        }
    }

    class FailedSanityCheck extends Condition implements BolusCalcCondition {
        public final String reason;
        FailedSanityCheck(String reason) {
            super("Sanity check failed: " + reason);
            this.reason = reason;
        }
    }

    class WaitingOnPrecondition extends Condition implements BolusCalcCondition {
        public final String thing;
        WaitingOnPrecondition(String thing) {
            super("Waiting for: " + thing);
            this.thing = thing;
        }
    }

    class Decision extends Condition implements BolusCalcCondition {
        public final String decision;
        Decision(String decision) {
            super(decision);
            this.decision = decision;
        }

        Decision(String decision, String because) {
            this(decision + " because " + because);
        }
    }


    class DataDecision extends Decision implements BolusCalcCondition {
        DataDecision(String decision) {
            super(decision);

        }

        DataDecision(String decision, String reason) {
            super(decision, reason);
        }
    }

    class NonActionDecision extends Decision implements BolusCalcCondition {
        NonActionDecision(String decision) {
            super(decision);
        }

        NonActionDecision(String decision, String reason) {
            super(decision, reason);
        }
    }

    class Condition extends Exception {
        private final String msg;
        Condition(String msg) {
            super(msg);
            this.msg = msg;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Condition)) return false;
            Condition condition = (Condition) o;
            return msg.equals(condition.msg);
        }

        @Override
        public int hashCode() {
            return Objects.hash(msg);
        }

        public String getMsg() {
            return msg;
        }
    }

    String getMsg();
}
