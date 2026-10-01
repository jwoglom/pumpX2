package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CgmJoinSessionFsl2HistoryLogTest {
    @Test
    public void testCgmJoinSessionFsl2HistoryLogParse() {
        CgmJoinSessionFsl2HistoryLog expected = new CgmJoinSessionFsl2HistoryLog(
                // long pumpTimeSec, long sequenceNum, long sessionStartTime, long sessionJoinTime, int sessionDuration, int sessionJoinReason
                566516808L, 506706L, 566516800L, 566516900L, 5, 6
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmJoinSessionFsl2HistoryLog);

        CgmJoinSessionFsl2HistoryLog parsedRes = (CgmJoinSessionFsl2HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getSessionStartTime(), parsedRes.getSessionStartTime());
        assertEquals(expected.getSessionJoinTime(), parsedRes.getSessionJoinTime());
        assertEquals(expected.getSessionDuration(), parsedRes.getSessionDuration());
        assertEquals(expected.getSessionJoinReason(), parsedRes.getSessionJoinReason());
    }
}
