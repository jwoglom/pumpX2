package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SyncDoseCompleteHistoryLogTest {
    @Test
    public void testSyncDoseCompleteHistoryLogParse() {
        SyncDoseCompleteHistoryLog expected = new SyncDoseCompleteHistoryLog(
                // long pumpTimeSec, long sequenceNum, int doseId, int doseSource, int tempAdjustmentPercent, float syncDoseSizeDelivered, int currentBasalRate, float activeInsulin
                566516808L, 506706L, 300, 2, 3, 2.75F, 304, 3.75F
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof SyncDoseCompleteHistoryLog);

        SyncDoseCompleteHistoryLog parsedRes = (SyncDoseCompleteHistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getDoseId(), parsedRes.getDoseId());
        assertEquals(expected.getDoseSource(), parsedRes.getDoseSource());
        assertEquals(expected.getTempAdjustmentPercent(), parsedRes.getTempAdjustmentPercent());
        assertEquals(expected.getSyncDoseSizeDelivered(), parsedRes.getSyncDoseSizeDelivered(), 0.0001F);
        assertEquals(expected.getCurrentBasalRate(), parsedRes.getCurrentBasalRate());
        assertEquals(expected.getActiveInsulin(), parsedRes.getActiveInsulin(), 0.0001F);
    }
}
