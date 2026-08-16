package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQUserModeChangeHistoryLogTest {
    @Test
    public void testControlIQUserModeChangeHistoryLog1() throws DecoderException {
        ControlIQUserModeChangeHistoryLog expected = (ControlIQUserModeChangeHistoryLog) new ControlIQUserModeChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, int currentUserMode, int previousUserMode,
            // int requestedAction, int sleepStartedByGui, int activeSleepSchedule,
            // int exerciseStoppedByTimer, int exerciseChoice, int exerciseTime, int eatingSoonStoppedByTimer
            579954015L, 456965L, 1, 2, 4, 0, 1, 0, 0, 0, 0
        ).withHeaderHighNibble(1);

        ControlIQUserModeChangeHistoryLog parsedRes = (ControlIQUserModeChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "e5105f65912205f9060001020400000100000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testControlIQUserModeChangeHistoryLog2() throws DecoderException {
        ControlIQUserModeChangeHistoryLog expected = (ControlIQUserModeChangeHistoryLog) new ControlIQUserModeChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, int currentUserMode, int previousUserMode,
            // int requestedAction, int sleepStartedByGui, int activeSleepSchedule,
            // int exerciseStoppedByTimer, int exerciseChoice, int exerciseTime, int eatingSoonStoppedByTimer
            579953995L, 456952L, 0, 1, 2, 1, 1, 0, 0, 0, 0
        ).withHeaderHighNibble(1);

        ControlIQUserModeChangeHistoryLog parsedRes = (ControlIQUserModeChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "e5104b659122f8f8060000010200010100000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
