package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CgmDataFsl3HistoryLogTest {
    @Test
    public void testCgmDataFsl3HistoryLogParse() {
        CgmDataFsl3HistoryLog expected = new CgmDataFsl3HistoryLog(
                // long pumpTimeSec, long sequenceNum, int status, int type, int rate, int rssi, int value, long timestamp, long transmitterTimestamp
                566516507L, 506696L, 0, 1, 13, -59, 261, 566516504L, 566510000L
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmDataFsl3HistoryLog);

        CgmDataFsl3HistoryLog parsedRes = (CgmDataFsl3HistoryLog) parsed;
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
