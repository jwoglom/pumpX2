package com.jwoglom.pumpx2.pump.messages.calculator;

import androidx.annotation.Nullable;

import java.util.Objects;



public interface BolusCalcCondition {
    BolusCalcCondition POSITIVE_BG_CORRECTION = new Decision("Adding positive BG correction", "above BG target",
            new BolusCalcPrompt("Your BG is above target: add correction bolus?", "Correction bolus added", "Correction bolus declined"));
    BolusCalcCondition NO_POSITIVE_BG_CORRECTION = new NonActionDecision("Not adding positive BG correction", "active IOB is greater than correction bolus while above target");
    BolusCalcCondition SET_ZERO_INSULIN = new Decision("Setting zero insulin", "negative correction greater than carb amount",
            new BolusCalcPrompt("Your BG is below target: reduce bolus calculation to zero?", "Bolus set to zero", "Zero bolus declined"));
    BolusCalcCondition NEGATIVE_BG_CORRECTION = new Decision("Adding negative BG correction", "below BG target",
            new BolusCalcPrompt("Your BG is below target: reduce bolus calculation?", "Bolus reduced", "Bolus reduction declined"));

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
        Decision(String decision) {
            super(decision);
        }

        Decision(String decision, String because) {
            super(decision, because);
        }

        Decision(String decision, String because, BolusCalcPrompt prompt) {
            super(decision, because, prompt);
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

    class BolusCalcPrompt {
        private final String promptMessage;
        private final String whenAcceptedNotice;
        private final String whenIgnoredNotice;
        BolusCalcPrompt(String promptMessage, String whenAcceptedNotice, String whenIgnoredNotice) {
            this.promptMessage = promptMessage;
            this.whenAcceptedNotice = whenAcceptedNotice;
            this.whenIgnoredNotice = whenIgnoredNotice;
        }

        public String getPromptMessage() {
            return promptMessage;
        }

        public String getWhenAcceptedNotice() {
            return whenAcceptedNotice;
        }

        public String getWhenIgnoredNotice() {
            return whenIgnoredNotice;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BolusCalcPrompt that = (BolusCalcPrompt) o;
            return Objects.equals(promptMessage, that.promptMessage) && Objects.equals(whenAcceptedNotice, that.whenAcceptedNotice) && Objects.equals(whenIgnoredNotice, that.whenIgnoredNotice);
        }

        @Override
        public int hashCode() {
            return Objects.hash(promptMessage, whenAcceptedNotice, whenIgnoredNotice);
        }
    }

    class Condition extends Exception implements Comparable<Condition> {
        private final String msg;
        private final String reason;
        private final BolusCalcPrompt prompt;
        Condition(String msg) {
            super(msg);
            this.msg = msg;
            this.reason = null;
            this.prompt = null;
        }

        Condition(String msg, String reason) {
            super(msg + " because " + reason);
            this.msg = msg;
            this.reason = reason;
            this.prompt = null;
        }

        Condition(String msg, String reason, BolusCalcPrompt prompt) {
            super(msg + " because " + reason);
            this.msg = msg;
            this.reason = reason;
            this.prompt = prompt;
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

        public @Nullable String getReason() {
            return reason;
        }

        public @Nullable BolusCalcPrompt getPrompt() {
            return prompt;
        }

        @Override
        public int compareTo(Condition o) {
            return o.hashCode() - this.hashCode();
        }
    }

    String getMsg();
    @Nullable String getReason();
    @Nullable BolusCalcPrompt getPrompt();
}
