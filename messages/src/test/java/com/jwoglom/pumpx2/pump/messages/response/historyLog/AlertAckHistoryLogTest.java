package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlertAckHistoryLogTest {
    @Test
    public void testAlertAckHistoryLog1() throws DecoderException {
        AlertAckHistoryLog expected = (AlertAckHistoryLog) new AlertAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580260938L, 469779L, 50
        ).withHeaderHighNibble(1);

        AlertAckHistoryLog parsedRes = (AlertAckHistoryLog) HistoryLogMessageTester.testSingle(
                "1b104a149622132b070032000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testAlertAckHistoryLog2() throws DecoderException {
        AlertAckHistoryLog expected = (AlertAckHistoryLog) new AlertAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580095340L, 462908L, 0
        ).withHeaderHighNibble(1);

        AlertAckHistoryLog parsedRes = (AlertAckHistoryLog) HistoryLogMessageTester.testSingle(
                "1b106c8d93223c10070000000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
