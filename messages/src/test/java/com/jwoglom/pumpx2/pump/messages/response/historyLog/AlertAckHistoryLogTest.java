package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlertAckHistoryLogTest {
    @Test
    public void testAlertAckHistoryLog1() throws DecoderException {
        AlertAckHistoryLog expected = new AlertAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580260938L, 469779L, 50
        );

        HistoryLogMessageTester.testSingle(
                "1b104a149622132b070032000000000000000000000000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }

    @Test
    public void testAlertAckHistoryLog2() throws DecoderException {
        AlertAckHistoryLog expected = new AlertAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580095340L, 462908L, 0
        );

        HistoryLogMessageTester.testSingle(
                "1b106c8d93223c10070000000000000000000000000000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
