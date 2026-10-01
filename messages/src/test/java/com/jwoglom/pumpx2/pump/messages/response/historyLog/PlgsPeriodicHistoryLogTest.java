package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PlgsPeriodicHistoryLogTest {
    @Test
    public void testPlgsPeriodicHistoryLogParse() {
        PlgsPeriodicHistoryLog expected = new PlgsPeriodicHistoryLog(
                // long pumpTimeSec, long sequenceNum, long timestamp, int fmr, int pgv, int fmrStatus, boolean pgvValid, int ruleState, int hoMinState, long status
                566516808L, 506706L, 566516800L, 301, 302, 4, true, 6, 3, 566517500L
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof PlgsPeriodicHistoryLog);

        PlgsPeriodicHistoryLog parsedRes = (PlgsPeriodicHistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getTimestamp(), parsedRes.getTimestamp());
        assertEquals(expected.getFmr(), parsedRes.getFmr());
        assertEquals(expected.getPgv(), parsedRes.getPgv());
        assertEquals(expected.getFmrStatus(), parsedRes.getFmrStatus());
        assertEquals(expected.isPgvValid(), parsedRes.isPgvValid());
        assertEquals(expected.getRuleState(), parsedRes.getRuleState());
        assertEquals(expected.getHoMinState(), parsedRes.getHoMinState());
        assertEquals(expected.getStatus(), parsedRes.getStatus());
    }
}
