package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
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

    /**
     * Byte layout per the Tandem Source event schema for LID_AA_DAILY_STATUS:
     * pumpControlState u8 @0, userMode u8 @1, sensorType u8 @2, weightUnit u8 @3, weight u16 @4, currentTdiPop u8 @6.
     */
    @Test
    public void testDailyStatusHistoryLogLayout() throws DecoderException {
        DailyStatusHistoryLog expected = new DailyStatusHistoryLog(
                // long pumpTimeSec, long sequenceNum, int sensorType, int userMode, int pumpControlState, int weightUnit, int weight, int currentTdiPop
                566516808L, 506706L, 3, 1, 2, 1, 180, 45
        );

        DailyStatusHistoryLog parsedRes = (DailyStatusHistoryLog) HistoryLogMessageTester.testSingle(
                // header: 0x0139 | pumpTime | seqNum || 02 01 03 01 | b400 | 2d | 00...
                "3901485cc42152bb070002010301b4002d000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(DailyStatusHistoryLog.PumpControlState.PCM_PINING, parsedRes.getPumpControlStateEnum());
        assertEquals(DailyStatusHistoryLog.UserMode.SLEEPING, parsedRes.getUserModeEnum());
        assertEquals(DailyStatusHistoryLog.SensorType.CGM_TYPE_DEXCOM_G7, parsedRes.getSensorTypeEnum());
        assertEquals(DailyStatusHistoryLog.WeightUnit.POUNDS, parsedRes.getWeightUnitEnum());
        assertEquals(180, parsedRes.getWeight());
        assertEquals(45, parsedRes.getCurrentTdiPop());
    }
}
