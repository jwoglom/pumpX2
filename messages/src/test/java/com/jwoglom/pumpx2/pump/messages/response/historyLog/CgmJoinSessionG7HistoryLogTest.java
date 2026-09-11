package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CgmJoinSessionG7HistoryLogTest {
    @Test
    public void testCgmJoinSessionG7HistoryLogParse() {
        CgmJoinSessionG7HistoryLog expected = new CgmJoinSessionG7HistoryLog(
                // long pumpTimeSec, long sequenceNum, long cgmTimestamp, long sessionSignature
                566516808L, 506706L, 566516805L, 123456789L
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmJoinSessionG7HistoryLog);

        CgmJoinSessionG7HistoryLog parsedRes = (CgmJoinSessionG7HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getCgmTimestamp(), parsedRes.getCgmTimestamp());
        assertEquals(expected.getSessionSignature(), parsedRes.getSessionSignature());
    }
}
