package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusDeliverySummaryHistoryLogTest {
    // Mobi driven by Trio: a 0.3 U bolus, the sixth within about an hour (earlier ones: 0.05,
    // 0.55, 0.45, 0.05 and 0.15 U, summing with this one to 1.55 U, i.e. 15.5 here).
    @Test
    public void testMobiSixthBolusInWindow() throws DecoderException {
        BolusDeliverySummaryHistoryLog expected = new BolusDeliverySummaryHistoryLog(
                // long pumpTimeSec, long sequenceNum, int unknownU8At10, int unknownU8At11, float unknownFloatAt14, float unknownFloatAt18, float unknownFloatAt22, int headerHighNibble
                590145454L, 672504L, 6, 27,
                Float.intBitsToFloat(0x40400001), // ~3.0
                Float.intBitsToFloat(0x40400001),
                15.5F,
                1
        );

        BolusDeliverySummaryHistoryLog parsedRes = (BolusDeliverySummaryHistoryLog) HistoryLogMessageTester.testSingle(
                "c210aee72c23f8420a00061b0000010040400100404000007841",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(6, parsedRes.getUnknownU8At10());
        assertEquals(27, parsedRes.getUnknownU8At11());
        assertEquals(15.5F, parsedRes.getUnknownFloatAt22(), 0.0F);
    }

    // Same pump 30 minutes later: 0.05 U, with four boluses (0.15, 0.3, 0.05, 0.05 U) left in the window.
    @Test
    public void testMobiWindowAfterOlderBolusesDropped() throws DecoderException {
        BolusDeliverySummaryHistoryLog expected = new BolusDeliverySummaryHistoryLog(
                590147253L, 672608L, 4, 29, 0.5F, 0.5F, 5.5F, 1
        );

        BolusDeliverySummaryHistoryLog parsedRes = (BolusDeliverySummaryHistoryLog) HistoryLogMessageTester.testSingle(
                "c210b5ee2c2360430a00041d00000000003f0000003f0000b040",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(4, parsedRes.getUnknownU8At10());
        assertEquals(29, parsedRes.getUnknownU8At11());
    }
}
