package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ParamChangeReminderHistoryLogTest {
    @Test
    public void testParamChangeReminderHistoryLog1() throws DecoderException {
        ParamChangeReminderHistoryLog expected = (ParamChangeReminderHistoryLog) new ParamChangeReminderHistoryLog(
            // long pumpTimeSec, long sequenceNum, int modification, int reminderId, int status, int enable, long frequencyMinutes, int startTime, int endTime, int activeDays
            580601544L, 483936L, 1, 2, 3, 1, 1380L, 0, 0, 0
        ).withHeaderHighNibble(1);

        ParamChangeReminderHistoryLog parsedRes = (ParamChangeReminderHistoryLog) HistoryLogMessageTester.testSingle(
                "6010c8469b226062070001020301640500000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testParamChangeReminderHistoryLog2() throws DecoderException {
        ParamChangeReminderHistoryLog expected = (ParamChangeReminderHistoryLog) new ParamChangeReminderHistoryLog(
            // long pumpTimeSec, long sequenceNum, int modification, int reminderId, int status, int enable, long frequencyMinutes, int startTime, int endTime, int activeDays
            580601544L, 483935L, 0, 2, 3, 1, 1380L, 0, 0, 0
        ).withHeaderHighNibble(1);

        ParamChangeReminderHistoryLog parsedRes = (ParamChangeReminderHistoryLog) HistoryLogMessageTester.testSingle(
                "6010c8469b225f62070000020301640500000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
