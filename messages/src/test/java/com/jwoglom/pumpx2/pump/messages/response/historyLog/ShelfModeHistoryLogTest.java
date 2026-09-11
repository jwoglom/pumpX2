package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ShelfModeHistoryLogTest {
    @Test
    public void testShelfModeHistoryLog1() throws DecoderException {
        ShelfModeHistoryLog expected = (ShelfModeHistoryLog) new ShelfModeHistoryLog(
            // long pumpTimeSec, long sequenceNum, long msecSinceReset, int lipoIbc, int lipoAbc, int lipoCurrent, long lipoRemCap, long lipoMv
            580777764L, 491136L, 1078644679L, 0, 0, 23130, 130L, 4176L
        ).withHeaderHighNibble(1);

        ShelfModeHistoryLog parsedRes = (ShelfModeHistoryLog) HistoryLogMessageTester.testSingle(
                "351024f79d22807e0700c7cf4a4000005a5a8200000050100000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
