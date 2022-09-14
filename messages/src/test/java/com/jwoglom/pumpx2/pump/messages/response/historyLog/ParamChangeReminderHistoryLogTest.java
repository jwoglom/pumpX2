package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.ParamChangeReminderHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ParamChangeReminderHistoryLogTest {
    @Test
    public void testParamChangeReminderHistoryLog() throws DecoderException {
        ParamChangeReminderHistoryLog expected = new ParamChangeReminderHistoryLog(
            // int modification, int reminderId, int status, int enable, long frequencyMinutes, int startTime, int endTime, int activeDays
        );

        ParamChangeReminderHistoryLog parsedRes = (ParamChangeReminderHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}