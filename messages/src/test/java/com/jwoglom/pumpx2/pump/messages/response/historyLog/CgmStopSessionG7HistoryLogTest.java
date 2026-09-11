package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CgmStopSessionG7HistoryLogTest {
    @Test
    public void testCgmStopSessionG7HistoryLogParse() {
        CgmStopSessionG7HistoryLog expected = new CgmStopSessionG7HistoryLog(
                // long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int stopSessionCode, int sessionStopReason, int sessionDuration
                566516808L, 506706L, 566516805L, 566430000L, 566516800L, 1, 2, 10
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmStopSessionG7HistoryLog);

        CgmStopSessionG7HistoryLog parsedRes = (CgmStopSessionG7HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getCurrentTransmitterTime(), parsedRes.getCurrentTransmitterTime());
        assertEquals(expected.getSessionStartTime(), parsedRes.getSessionStartTime());
        assertEquals(expected.getSessionStopTime(), parsedRes.getSessionStopTime());
        assertEquals(expected.getStopSessionCode(), parsedRes.getStopSessionCode());
        assertEquals(expected.getSessionStopReason(), parsedRes.getSessionStopReason());
        assertEquals(expected.getSessionDuration(), parsedRes.getSessionDuration());
    }
}
