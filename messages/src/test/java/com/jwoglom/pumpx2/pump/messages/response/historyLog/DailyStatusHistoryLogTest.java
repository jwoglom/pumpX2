package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DailyStatusHistoryLogTest {
    @Test
    public void testDailyStatusHistoryLogParse() {
        DailyStatusHistoryLog expected = new DailyStatusHistoryLog(
                // long pumpTimeSec, long sequenceNum, int sensorType, int userMode, int pumpControlState
                566516808L, 506706L, 2, 1, 3
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof DailyStatusHistoryLog);

        DailyStatusHistoryLog parsedRes = (DailyStatusHistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getSensorType(), parsedRes.getSensorType());
        assertEquals(expected.getUserMode(), parsedRes.getUserMode());
        assertEquals(expected.getPumpControlState(), parsedRes.getPumpControlState());
        assertEquals(expected.getSensorTypeEnum(), parsedRes.getSensorTypeEnum());
        assertEquals(expected.getUserModeEnum(), parsedRes.getUserModeEnum());
        assertEquals(expected.getPumpControlStateEnum(), parsedRes.getPumpControlStateEnum());
        assertEquals(DailyStatusHistoryLog.SensorType.CGM_TYPE_LIBRE2, parsedRes.getSensorTypeEnum());
        assertEquals(DailyStatusHistoryLog.UserMode.SLEEPING, parsedRes.getUserModeEnum());
        assertEquals(DailyStatusHistoryLog.PumpControlState.PCM_CLOSED_LOOP, parsedRes.getPumpControlStateEnum());
    }
}
