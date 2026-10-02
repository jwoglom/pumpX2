package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CgmStartSessionFsl2HistoryLogTest {
    @Test
    public void testCgmStartSessionFsl2HistoryLogParse() {
        CgmStartSessionFsl2HistoryLog expected = new CgmStartSessionFsl2HistoryLog(
                // long pumpTimeSec, long sequenceNum, long sessionStartTime, int sessionDuration
                566516808L, 506706L, 566516800L, 4
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmStartSessionFsl2HistoryLog);

        CgmStartSessionFsl2HistoryLog parsedRes = (CgmStartSessionFsl2HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getSessionStartTime(), parsedRes.getSessionStartTime());
        assertEquals(expected.getSessionDuration(), parsedRes.getSessionDuration());
    }
}
