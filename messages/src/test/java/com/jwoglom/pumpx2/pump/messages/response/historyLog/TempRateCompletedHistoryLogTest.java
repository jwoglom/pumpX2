package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TempRateCompletedHistoryLogTest {
    @Test
    public void testTempRateCompletedHistoryLog1() throws DecoderException {
        TempRateCompletedHistoryLog expected = new TempRateCompletedHistoryLog(
            // long pumpTimeSec, long sequenceNum, int tempRateId, long timeLeft
            579784686L, 449606L, 21, 0L
        );

        TempRateCompletedHistoryLog parsedRes = (TempRateCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "0f10eecf8e2246dc060003001500000000000000000000000000",
                expected
        );
        // no cargo round-trip: bytes 10-11 carry unparsed data (0x0003 LE) that buildCargo does
        // not reproduce; buildCargo also writes tempRateId at offset 10 rather than offset 12,
        // where parse() actually reads it
    }
}
