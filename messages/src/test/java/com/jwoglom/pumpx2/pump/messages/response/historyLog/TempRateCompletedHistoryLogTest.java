package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.TempRateCompletedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TempRateCompletedHistoryLogTest {
    @Test
    public void testTempRateCompletedHistoryLog() throws DecoderException {
        TempRateCompletedHistoryLog expected = new TempRateCompletedHistoryLog(
            // int tempRateId, long timeLeft
        );

        TempRateCompletedHistoryLog parsedRes = (TempRateCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}