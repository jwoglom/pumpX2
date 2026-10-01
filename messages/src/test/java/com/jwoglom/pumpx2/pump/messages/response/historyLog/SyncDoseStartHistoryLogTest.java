package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SyncDoseStartHistoryLogTest {
    @Test
    public void testSyncDoseStartHistoryLogParse() {
        SyncDoseStartHistoryLog expected = new SyncDoseStartHistoryLog(
                // long pumpTimeSec, long sequenceNum, int doseId, int doseSource, int tempAdjustmentPercent, float syncDoseSizeRequested, int currentBasalRate, int profileBasalRate, float activeInsulin
                566516808L, 506706L, 300, 2, 3, 2.75F, 304, 305, 4.25F
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof SyncDoseStartHistoryLog);

        SyncDoseStartHistoryLog parsedRes = (SyncDoseStartHistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getDoseId(), parsedRes.getDoseId());
        assertEquals(expected.getDoseSource(), parsedRes.getDoseSource());
        assertEquals(expected.getTempAdjustmentPercent(), parsedRes.getTempAdjustmentPercent());
        assertEquals(expected.getSyncDoseSizeRequested(), parsedRes.getSyncDoseSizeRequested(), 0.0001F);
        assertEquals(expected.getCurrentBasalRate(), parsedRes.getCurrentBasalRate());
        assertEquals(expected.getProfileBasalRate(), parsedRes.getProfileBasalRate());
        assertEquals(expected.getActiveInsulin(), parsedRes.getActiveInsulin(), 0.0001F);
    }
}
