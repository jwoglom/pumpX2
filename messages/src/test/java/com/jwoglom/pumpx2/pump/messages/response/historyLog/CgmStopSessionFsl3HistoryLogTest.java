package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CgmStopSessionFsl3HistoryLogTest {
    @Test
    public void testCgmStopSessionFsl3HistoryLogParse() {
        CgmStopSessionFsl3HistoryLog expected = new CgmStopSessionFsl3HistoryLog(
                // long pumpTimeSec, long sequenceNum, long sessionStartTime, long sessionStopTime, int sessionDuration, int sessionStopReason
                566516808L, 506706L, 566516800L, 566516900L, 5, 6
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmStopSessionFsl3HistoryLog);

        CgmStopSessionFsl3HistoryLog parsedRes = (CgmStopSessionFsl3HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getSessionStartTime(), parsedRes.getSessionStartTime());
        assertEquals(expected.getSessionStopTime(), parsedRes.getSessionStopTime());
        assertEquals(expected.getSessionDuration(), parsedRes.getSessionDuration());
        assertEquals(expected.getSessionStopReason(), parsedRes.getSessionStopReason());
    }
}
