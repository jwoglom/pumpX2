package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HistoryRequestRangeHistoryLogTest {
    @Test
    public void testHistoryRequestRange_mobi() throws DecoderException {
        // Maintainer's Tandem Mobi. In the two hours before this record TandemKit sent 27
        // HistoryLogRequests with startLog from 670898 to 671152 and received 289 records.
        HistoryRequestRangeHistoryLog expected = (HistoryRequestRangeHistoryLog) new HistoryRequestRangeHistoryLog(
                // long pumpTimeSec, long sequenceNum, long unknown10, long minStartLog, long maxStartLog, int unknown22, int unknown24
                590119266L, 671181L, 913L, 670898L, 671152L, 0, 26
        ).withHeaderHighNibble(1);

        HistoryRequestRangeHistoryLog parsedRes = (HistoryRequestRangeHistoryLog) HistoryLogMessageTester.testSingle(
                "eb1162812c23cd3d0a0091030000b23c0a00b03d0a0000001a00",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(670898L, parsedRes.getMinStartLog());
        assertEquals(671152L, parsedRes.getMaxStartLog());
        assertEquals(26, parsedRes.getUnknown24());
    }

    @Test
    public void testHistoryRequestRange_issueExample() throws DecoderException {
        HistoryRequestRangeHistoryLog expected = (HistoryRequestRangeHistoryLog) new HistoryRequestRangeHistoryLog(
                589528866L, 640817L, 533L, 640509L, 640810L, 0, 33
        ).withHeaderHighNibble(1);

        HistoryRequestRangeHistoryLog parsedRes = (HistoryRequestRangeHistoryLog) HistoryLogMessageTester.testSingle(
                "eb11227f232331c7090015020000fdc509002ac7090000002100",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
