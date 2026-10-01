package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AntDailyStatus1HistoryLogTest {
    @Test
    public void testAntDailyStatus1HistoryLogParse() {
        AntDailyStatus1HistoryLog expected = new AntDailyStatus1HistoryLog(
                // long pumpTimeSec, long sequenceNum, boolean closedLoopPreferred, int totalDailyInsulinUnits, int glycemicGoal, int priorTherapy, int sleepEatSegmentStartTime0, int sleepEatSegmentStartTime1, int sleepEatSegmentStartTime2, int sleepEatSegmentStartTime3, int sleepEatSegmentType0, int sleepEatSegmentType1, int sleepEatSegmentType2, int sleepEatSegmentType3
                566516808L, 506706L, true, 2, 2, 2, 304, 305, 306, 307, 6, 6, 6, 6
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof AntDailyStatus1HistoryLog);

        AntDailyStatus1HistoryLog parsedRes = (AntDailyStatus1HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.isClosedLoopPreferred(), parsedRes.isClosedLoopPreferred());
        assertEquals(expected.getTotalDailyInsulinUnits(), parsedRes.getTotalDailyInsulinUnits());
        assertEquals(expected.getGlycemicGoal(), parsedRes.getGlycemicGoal());
        assertEquals(expected.getPriorTherapy(), parsedRes.getPriorTherapy());
        assertEquals(expected.getSleepEatSegmentStartTime0(), parsedRes.getSleepEatSegmentStartTime0());
        assertEquals(expected.getSleepEatSegmentStartTime1(), parsedRes.getSleepEatSegmentStartTime1());
        assertEquals(expected.getSleepEatSegmentStartTime2(), parsedRes.getSleepEatSegmentStartTime2());
        assertEquals(expected.getSleepEatSegmentStartTime3(), parsedRes.getSleepEatSegmentStartTime3());
        assertEquals(expected.getSleepEatSegmentType0(), parsedRes.getSleepEatSegmentType0());
        assertEquals(expected.getSleepEatSegmentType1(), parsedRes.getSleepEatSegmentType1());
        assertEquals(expected.getSleepEatSegmentType2(), parsedRes.getSleepEatSegmentType2());
        assertEquals(expected.getSleepEatSegmentType3(), parsedRes.getSleepEatSegmentType3());
    }
}
