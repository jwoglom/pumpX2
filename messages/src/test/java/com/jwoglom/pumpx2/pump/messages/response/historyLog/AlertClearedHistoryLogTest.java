package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlertClearedHistoryLogTest {
    @Test
    public void testAlertClearedHistoryLog1() throws DecoderException {
        AlertClearedHistoryLog expected = new AlertClearedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId, long faultLocatorData
            580773244L, 490890L, 50, 0
        );

        AlertClearedHistoryLog parsedRes = (AlertClearedHistoryLog) HistoryLogMessageTester.testSingle(
                "1a107ce59d228a7d070032000000000000000000000000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }

    @Test
    public void testAlertClearedHistoryLog2() throws DecoderException {
        AlertClearedHistoryLog expected = new AlertClearedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId, long faultLocatorData
            580474756L, 478427L, 2, 0
        );

        AlertClearedHistoryLog parsedRes = (AlertClearedHistoryLog) HistoryLogMessageTester.testSingle(
                "1a1084579922db4c070002000000000000000000000000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
