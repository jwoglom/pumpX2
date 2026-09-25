package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HistoryRequestCountersHistoryLogTest {
    @Test
    public void testHistoryRequestCounters_mobi() throws DecoderException {
        // Maintainer's Tandem Mobi. TandemKit sent 27 HistoryLogRequests in the two hours before this record.
        HistoryRequestCountersHistoryLog expected = (HistoryRequestCountersHistoryLog) new HistoryRequestCountersHistoryLog(
                // long pumpTimeSec, long sequenceNum, long historyLogRequestCount, long unknown14, long unknown18, long unknown22
                590119266L, 671180L, 27L, 0L, 8970L, 280L
        ).withHeaderHighNibble(1);

        HistoryRequestCountersHistoryLog parsedRes = (HistoryRequestCountersHistoryLog) HistoryLogMessageTester.testSingle(
                "ea1162812c23cc3d0a001b000000000000000a23000018010000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(27L, parsedRes.getHistoryLogRequestCount());
    }

    @Test
    public void testHistoryRequestCounters_issueExample() throws DecoderException {
        HistoryRequestCountersHistoryLog expected = (HistoryRequestCountersHistoryLog) new HistoryRequestCountersHistoryLog(
                589528866L, 640816L, 34L, 0L, 6665L, 198L
        ).withHeaderHighNibble(1);

        HistoryRequestCountersHistoryLog parsedRes = (HistoryRequestCountersHistoryLog) HistoryLogMessageTester.testSingle(
                "ea11227f232330c709002200000000000000091a0000c6000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
