package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TempAdjustmentCompletedHistoryLogTest {
    @Test
    public void testTempAdjustmentCompletedHistoryLogParse() {
        TempAdjustmentCompletedHistoryLog expected = new TempAdjustmentCompletedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int stopReason, int tempRateId, long timeLeft
                566516808L, 506706L, 300, 301, 566517000L
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof TempAdjustmentCompletedHistoryLog);

        TempAdjustmentCompletedHistoryLog parsedRes = (TempAdjustmentCompletedHistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getStopReason(), parsedRes.getStopReason());
        assertEquals(expected.getTempRateId(), parsedRes.getTempRateId());
        assertEquals(expected.getTimeLeft(), parsedRes.getTimeLeft());
    }
}
