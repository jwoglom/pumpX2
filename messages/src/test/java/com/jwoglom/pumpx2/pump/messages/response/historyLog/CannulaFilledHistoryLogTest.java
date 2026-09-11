package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CannulaFilledHistoryLogTest {
    @Test
    public void testCannulaFilledHistoryLog1() throws DecoderException {
        CannulaFilledHistoryLog expected = (CannulaFilledHistoryLog) new CannulaFilledHistoryLog(
            // long pumpTimeSec, long sequenceNum, float primeSize, long completionStatus
            580601541L, 483933L, 0.3F, 3L
        ).withHeaderHighNibble(1);

        CannulaFilledHistoryLog parsedRes = (CannulaFilledHistoryLog) HistoryLogMessageTester.testSingle(
                "3d10c5469b225d6207009a99993e030000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCannulaFilledHistoryLog2() throws DecoderException {
        CannulaFilledHistoryLog expected = (CannulaFilledHistoryLog) new CannulaFilledHistoryLog(
            // long pumpTimeSec, long sequenceNum, float primeSize, long completionStatus
            579744849L, 448176L, 0.3F, 3L
        ).withHeaderHighNibble(1);

        CannulaFilledHistoryLog parsedRes = (CannulaFilledHistoryLog) HistoryLogMessageTester.testSingle(
                "3d1051348e22b0d606009a99993e030000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
