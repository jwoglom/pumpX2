package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.TimeChangedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TimeChangedHistoryLogTest {
    @Test
    public void testTimeChangedHistoryLog() throws DecoderException {
        TimeChangedHistoryLog expected = new TimeChangedHistoryLog(
            // long timePrior, long timeAfter, long rawRTC
        );

        TimeChangedHistoryLog parsedRes = (TimeChangedHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}