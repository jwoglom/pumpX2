package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionsAHistoryLogTest {
    @Test
    public void testVersionsAHistoryLogParse() {
        VersionsAHistoryLog expected = new VersionsAHistoryLog(
                // long pumpTimeSec, long sequenceNum, long armPartNumber, long armSwVersion, long blePartNumber, long bleSwVersion
                566516808L, 506706L, 1002345L, 30405060L, 2003456L, 40506070L
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof VersionsAHistoryLog);

        VersionsAHistoryLog parsedRes = (VersionsAHistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getArmPartNumber(), parsedRes.getArmPartNumber());
        assertEquals(expected.getArmSwVersion(), parsedRes.getArmSwVersion());
        assertEquals(expected.getBlePartNumber(), parsedRes.getBlePartNumber());
        assertEquals(expected.getBleSwVersion(), parsedRes.getBleSwVersion());
    }
}
