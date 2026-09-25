package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BleLinkStatsHistoryLogTest {
    // Maintainer's Tandem Mobi. Header high nibble 1.

    @Test
    public void testBleLinkStats_issueExample() throws DecoderException {
        BleLinkStatsHistoryLog expected = (BleLinkStatsHistoryLog) new BleLinkStatsHistoryLog(
                // long pumpTimeSec, long sequenceNum, int minimum, int maximum, int unknown12, int unknown13, int unknown14, int unknown15, int unknown16, int unknown18, int intervalSeconds
                589528867L, 640818L, -86, -49, -64, -70, -65, -60, 56, 1835, 3600
        ).withHeaderHighNibble(1);

        BleLinkStatsHistoryLog parsedRes = (BleLinkStatsHistoryLog) HistoryLogMessageTester.testSingle(
                "d911237f232332c70900aacfc0babfc438002b07100e00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(-86, parsedRes.getMinimum());
        assertEquals(-49, parsedRes.getMaximum());
        assertEquals(3600, parsedRes.getIntervalSeconds());
    }

    @Test
    public void testBleLinkStats_orderedStatistics() throws DecoderException {
        BleLinkStatsHistoryLog expected = (BleLinkStatsHistoryLog) new BleLinkStatsHistoryLog(
                590112070L, 670882L, -92, -51, -73, -80, -75, -70, 54, 2264, 3600
        ).withHeaderHighNibble(1);

        BleLinkStatsHistoryLog parsedRes = (BleLinkStatsHistoryLog) HistoryLogMessageTester.testSingle(
                "d91146652c23a23c0a00a4cdb7b0b5ba3600d808100e00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertTrue(parsedRes.getMinimum() <= parsedRes.getUnknown13());
        assertTrue(parsedRes.getUnknown13() <= parsedRes.getUnknown12());
        assertTrue(parsedRes.getUnknown12() <= parsedRes.getUnknown15());
        assertTrue(parsedRes.getUnknown15() <= parsedRes.getMaximum());
    }

    @Test
    public void testBleLinkStats_intervalOneSecondLong() throws DecoderException {
        // 3600 s of pump time after the previous 473 record, but the record says 3601.
        BleLinkStatsHistoryLog expected = (BleLinkStatsHistoryLog) new BleLinkStatsHistoryLog(
                590184070L, 674456L, -89, -52, -68, -75, -70, -65, 43, 2448, 3601
        ).withHeaderHighNibble(1);

        BleLinkStatsHistoryLog parsedRes = (BleLinkStatsHistoryLog) HistoryLogMessageTester.testSingle(
                "d911867e2d23984a0a00a7ccbcb5babf2b009009110e00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(3601, parsedRes.getIntervalSeconds());
    }

    @Test
    public void testBleLinkStats_intervalOneSecondShort() throws DecoderException {
        BleLinkStatsHistoryLog expected = (BleLinkStatsHistoryLog) new BleLinkStatsHistoryLog(
                590187670L, 674672L, -88, -53, -68, -75, -70, -65, 42, 2505, 3599
        ).withHeaderHighNibble(1);

        BleLinkStatsHistoryLog parsedRes = (BleLinkStatsHistoryLog) HistoryLogMessageTester.testSingle(
                "d911968c2d23704b0a00a8cbbcb5babf2a00c9090f0e00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(3599, parsedRes.getIntervalSeconds());
    }
}
