package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.TimeChangeHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TimeChangeHistoryLogTest {
    @Test
    public void testTimeChangeHistoryLog() throws DecoderException {
        TimeChangeHistoryLog expected = new TimeChangeHistoryLog(
                new byte[]{13,0,28,-54,-77,26,59,11},
                // long timePrior, long timeAfter, long rawRTC
                1114832896L, 559939677L, 2667905120L,
                new byte[]{96, 50, 0, 0, 0, 0}
        );

        HistoryLogStreamResponse parsedMessage = HistoryLogMessageTester.test(
                "000081001c01100d001ccab31a3b0b000073425d0060216000059f60320000000033f9",
                16,
                ImmutableList.of(expected)
        );

        TimeChangeHistoryLog parsedRes = (TimeChangeHistoryLog) parsedMessage.getHistoryLogs().stream().findFirst().get();
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}