package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.DateChangeHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class DateChangeHistoryLogTest {
    @Test
    @Ignore("needs hex input")
    public void testDateChangeHistoryLog() throws DecoderException {
        DateChangeHistoryLog expected = new DateChangeHistoryLog(
            // long datePrior, long dateAfter, long rawRTCTime
        );

        HistoryLogStreamResponse parsedMessage = HistoryLogMessageTester.test(
                "xxxx",
                0,
                ImmutableList.of(expected)
        );

        DateChangeHistoryLog parsedRes = (DateChangeHistoryLog) parsedMessage.getHistoryLogs().stream().findFirst().get();
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}