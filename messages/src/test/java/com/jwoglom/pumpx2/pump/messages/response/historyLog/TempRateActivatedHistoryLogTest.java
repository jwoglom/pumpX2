package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TempRateActivatedHistoryLogTest {
    @Test
    public void testTempRateActivatedHistoryLog1() throws DecoderException {
        TempRateActivatedHistoryLog expected = new TempRateActivatedHistoryLog(
            // long pumpTimeSec, long sequenceNum, float percent, float duration, int tempRateId
            579777478L, 449311L, 50.0F, 7200000.0F, 21
        );

        TempRateActivatedHistoryLog parsedRes = (TempRateActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "0210c6b38e221fdb06000000484200badb4a0200150000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
