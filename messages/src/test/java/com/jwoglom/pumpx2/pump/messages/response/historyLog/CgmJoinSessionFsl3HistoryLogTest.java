package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CgmJoinSessionFsl3HistoryLogTest {
    @Test
    public void testCgmJoinSessionFsl3HistoryLogParse() {
        CgmJoinSessionFsl3HistoryLog expected = new CgmJoinSessionFsl3HistoryLog(
                // long pumpTimeSec, long sequenceNum, long sessionStartTime, long sessionJoinTime, int sessionDuration, int sessionJoinReason, long sessionDurationSecs
                566516808L, 506706L, 566516800L, 566516900L, 5, 6, 566517200L
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmJoinSessionFsl3HistoryLog);

        CgmJoinSessionFsl3HistoryLog parsedRes = (CgmJoinSessionFsl3HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getSessionStartTime(), parsedRes.getSessionStartTime());
        assertEquals(expected.getSessionJoinTime(), parsedRes.getSessionJoinTime());
        assertEquals(expected.getSessionDuration(), parsedRes.getSessionDuration());
        assertEquals(expected.getSessionJoinReason(), parsedRes.getSessionJoinReason());
        assertEquals(expected.getSessionDurationSecs(), parsedRes.getSessionDurationSecs());
    }
}
