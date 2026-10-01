package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AntDailyStatus2HistoryLogTest {
    @Test
    public void testAntDailyStatus2HistoryLogParse() {
        AntDailyStatus2HistoryLog expected = new AntDailyStatus2HistoryLog(
                // long pumpTimeSec, long sequenceNum, int pumpControlState, int sensorType
                566516808L, 506706L, 1, 2
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof AntDailyStatus2HistoryLog);

        AntDailyStatus2HistoryLog parsedRes = (AntDailyStatus2HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getPumpControlState(), parsedRes.getPumpControlState());
        assertEquals(expected.getSensorType(), parsedRes.getSensorType());
    }
}
