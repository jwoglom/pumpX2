package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TempAdjustmentActivatedHistoryLogTest {
    @Test
    public void testTempAdjustmentActivatedHistoryLogParse() {
        TempAdjustmentActivatedHistoryLog expected = new TempAdjustmentActivatedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int requestedPercent, int tempBasalPercent, long duration, int requestedAdjustmentType, int tempRateId
                566516808L, 506706L, 300, 301, 566517000L, 303, 304
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof TempAdjustmentActivatedHistoryLog);

        TempAdjustmentActivatedHistoryLog parsedRes = (TempAdjustmentActivatedHistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getRequestedPercent(), parsedRes.getRequestedPercent());
        assertEquals(expected.getTempBasalPercent(), parsedRes.getTempBasalPercent());
        assertEquals(expected.getDuration(), parsedRes.getDuration());
        assertEquals(expected.getRequestedAdjustmentType(), parsedRes.getRequestedAdjustmentType());
        assertEquals(expected.getTempRateId(), parsedRes.getTempRateId());
    }
}
