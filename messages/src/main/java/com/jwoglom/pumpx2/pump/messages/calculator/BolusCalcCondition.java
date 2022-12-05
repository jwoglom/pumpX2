package com.jwoglom.pumpx2.pump.messages.calculator;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface BolusCalcCondition {
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
            super("Waiting for data: " + thing);
            this.thing = thing;
        }
    }

    class Decision extends Condition implements BolusCalcCondition {
        public final String decidedTo;
        Decision(String decidedTo) {
            super(decidedTo);
            this.decidedTo = decidedTo;
        }

        Decision(String decidedTo, String reason) {
            this(decidedTo + " because: " + reason);
        }
    }


    class DataDecision extends Decision implements BolusCalcCondition {
        DataDecision(String decidedTo) {
            super(decidedTo);

        }

        DataDecision(String decidedTo, String reason) {
            super(decidedTo, reason);
        }
    }

    class NonActionDecision extends Decision implements BolusCalcCondition {
        NonActionDecision(String decidedToNot) {
            super("not " + decidedToNot);

        }

        NonActionDecision(String decidedToNot, String reason) {
            super("not " + decidedToNot, reason);
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
