package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CgmDataFsl2HistoryLogTest {
    @Test
    public void testCgmDataFsl2HistoryLogParse() {
        CgmDataFsl2HistoryLog expected = new CgmDataFsl2HistoryLog(
                // long pumpTimeSec, long sequenceNum, int status, int type, int rate, int rssi, int value, long timestamp, long transmitterTimestamp
                566516808L, 506706L, 3, 1, 4, -61, 257, 566516805L, 566512000L
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmDataFsl2HistoryLog);

        CgmDataFsl2HistoryLog parsedRes = (CgmDataFsl2HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getStatus(), parsedRes.getStatus());
        assertEquals(expected.getType(), parsedRes.getType());
        assertEquals(expected.getRate(), parsedRes.getRate());
        assertEquals(expected.getRssi(), parsedRes.getRssi());
        assertEquals(expected.getValue(), parsedRes.getValue());
        assertEquals(expected.getTimestamp(), parsedRes.getTimestamp());
        assertEquals(expected.getTransmitterTimestamp(), parsedRes.getTransmitterTimestamp());
    }
}
