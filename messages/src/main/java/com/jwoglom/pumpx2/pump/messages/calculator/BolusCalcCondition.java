package com.jwoglom.pumpx2.pump.messages.calculator;

import java.util.Objects;

import javax.annotation.Nullable;

public interface BolusCalcCondition {
    BolusCalcCondition POSITIVE_BG_CORRECTION = new Decision("Adding positive BG correction", "above BG target", "Your BG is above target: add correction bolus?");
    BolusCalcCondition NO_POSITIVE_BG_CORRECTION = new NonActionDecision("Not adding positive BG correction", "active IOB is greater than correction bolus while above target");
    BolusCalcCondition SET_ZERO_INSULIN = new Decision("Setting zero insulin", "negative correction greater than carb amount", "Your BG is below target: reduce bolus calculation to zero?");
    BolusCalcCondition NEGATIVE_BG_CORRECTION = new Decision("Adding negative BG correction", "below BG target", "Your BG is below target: reduce bolus calculation?");

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

        Decision(String decision, String because, String promptQuestion) {
            super(decision + " because " + because, promptQuestion);
            this.decision = decision;
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
        NonActionDecision(String decision, String reason) {
            super(decision, reason);
        }
    }

    class IgnoredDecision extends Decision implements BolusCalcCondition {
        IgnoredDecision(BolusCalcCondition decision) {
            super("Ignoring '" + decision.getMsg() + "'", "ignored by user");
        }
    }

    class Condition extends Exception {
        private final String msg;
        private final String promptQuestion;
        Condition(String msg) {
            super(msg);
            this.msg = msg;
            this.promptQuestion = null;
        }

        Condition(String msg, String promptQuestion) {
            super(msg);
            this.msg = msg;
            this.promptQuestion = promptQuestion;
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

        public @Nullable String getPromptQuestion() {
            return promptQuestion;
        }
    }

    String getMsg();
    @Nullable String getPromptQuestion();
}
