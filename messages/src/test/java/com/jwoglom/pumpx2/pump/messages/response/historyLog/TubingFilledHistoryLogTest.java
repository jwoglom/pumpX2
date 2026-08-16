package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TubingFilledHistoryLogTest {
    // All ten captured samples for this record type carry primeSize == -1.0F (bytes 000080bf)
    // alongside completionStatus == COMPLETED (3); this looks like a firmware sentinel rather
    // than a decode error, since the IEEE-754 bit pattern is exact and consistent across every
    // sample. Left in verbatim per the capture rather than assumed a bug.
    @Test
    public void testTubingFilledHistoryLog1() throws DecoderException {
        TubingFilledHistoryLog expected = (TubingFilledHistoryLog) new TubingFilledHistoryLog(
            // long pumpTimeSec, long sequenceNum, float primeSize, long completionStatus, long position
            580734269L, 489155L, -1.0F, 3L, 662652L
        ).withHeaderHighNibble(1);

        TubingFilledHistoryLog parsedRes = (TubingFilledHistoryLog) HistoryLogMessageTester.testSingle(
                "3f103d4d9d22c3760700000080bf030000007c1c0a0000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testTubingFilledHistoryLog2() throws DecoderException {
        TubingFilledHistoryLog expected = (TubingFilledHistoryLog) new TubingFilledHistoryLog(
            // long pumpTimeSec, long sequenceNum, float primeSize, long completionStatus, long position
            579743459L, 448074L, -1.0F, 3L, 547509L
        ).withHeaderHighNibble(1);

        TubingFilledHistoryLog parsedRes = (TubingFilledHistoryLog) HistoryLogMessageTester.testSingle(
                "3f10e32e8e224ad60600000080bf03000000b55a080000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
