package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlertClearedHistoryLogTest {
    @Test
    public void testAlertClearedHistoryLog1() throws DecoderException {
        AlertClearedHistoryLog expected = (AlertClearedHistoryLog) new AlertClearedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId, long faultLocatorData
            580773244L, 490890L, 50, 0
        ).withHeaderHighNibble(1);

        AlertClearedHistoryLog parsedRes = (AlertClearedHistoryLog) HistoryLogMessageTester.testSingle(
                "1a107ce59d228a7d070032000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testAlertClearedHistoryLog2() throws DecoderException {
        AlertClearedHistoryLog expected = (AlertClearedHistoryLog) new AlertClearedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId, long faultLocatorData
            580474756L, 478427L, 2, 0
        ).withHeaderHighNibble(1);

        AlertClearedHistoryLog parsedRes = (AlertClearedHistoryLog) HistoryLogMessageTester.testSingle(
                "1a1084579922db4c070002000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
