package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.response.historyLog.CgmStateChangeGxHistoryLog.Event;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.CgmStateChangeGxHistoryLog.State;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmStateChangeGxHistoryLogTest {

    @Test
    public void testCgmStateChange_idleTickWithoutSession() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2), no CGM session. Header high nibble 1.
        CgmStateChangeGxHistoryLog expected = (CgmStateChangeGxHistoryLog) new CgmStateChangeGxHistoryLog(
                // long pumpTimeSec, long sequenceNum, int previousStateId, int stateId, int eventId
                590112364L, 670898L, 0, 0, 6
        ).withHeaderHighNibble(1);

        CgmStateChangeGxHistoryLog parsedRes = (CgmStateChangeGxHistoryLog) HistoryLogMessageTester.testSingle(
                "db106c662c23b23c0a0000000000000000000000000006000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(State.STOPPED, parsedRes.getPreviousState());
        assertEquals(State.STOPPED, parsedRes.getState());
        assertEquals(Event.IDLE_TICK, parsedRes.getEvent());
        assertFalse(parsedRes.isStateChange());
        assertEquals(0, parsedRes.getUnknown11());
        assertEquals(0, parsedRes.getUnknown15());
        assertEquals(0L, parsedRes.getUnknown18());
        assertEquals(0, parsedRes.getUnknown23());
    }

    @Test
    public void testCgmStateChange_cycleStartThenDataReceived() throws DecoderException {
        // Maintainer's t:slim X2 (2024), Dexcom G7 session. Header high nibble 0.
        CgmStateChangeGxHistoryLog expectedStart = new CgmStateChangeGxHistoryLog(
                534413222L, 1061528L, 2, 2, 1
        );
        CgmStateChangeGxHistoryLog parsedStart = (CgmStateChangeGxHistoryLog) HistoryLogMessageTester.testSingle(
                "db00a67fda1f9832100002000000020000000000000001000000",
                expectedStart
        );
        assertHexEquals(expectedStart.getCargo(), parsedStart.getCargo());
        assertEquals(State.ACTIVE, parsedStart.getState());
        assertEquals(Event.CYCLE_START, parsedStart.getEvent());

        // The next record, 1 s later; the DexcomG7CGM reading follows it in the same second.
        CgmStateChangeGxHistoryLog expectedData = new CgmStateChangeGxHistoryLog(
                534413223L, 1061529L, 2, 2, 2
        );
        CgmStateChangeGxHistoryLog parsedData = (CgmStateChangeGxHistoryLog) HistoryLogMessageTester.testSingle(
                "db00a77fda1f9932100002000000020000000000000002000000",
                expectedData
        );
        assertHexEquals(expectedData.getCargo(), parsedData.getCargo());
        assertEquals(State.ACTIVE, parsedData.getState());
        assertEquals(Event.DATA_RECEIVED, parsedData.getEvent());
        assertEquals(1, parsedData.getPumpTimeSec() - parsedStart.getPumpTimeSec());

        // Maintainer's Tandem Mobi (2025), Dexcom G7 session. Header high nibble 1.
        CgmStateChangeGxHistoryLog expectedMobi = (CgmStateChangeGxHistoryLog) new CgmStateChangeGxHistoryLog(
                539479615L, 106080L, 2, 2, 2
        ).withHeaderHighNibble(1);
        CgmStateChangeGxHistoryLog parsedMobi = (CgmStateChangeGxHistoryLog) HistoryLogMessageTester.testSingle(
                "db103fce2720609e010002000000020000000000000002000000",
                expectedMobi
        );
        assertHexEquals(expectedMobi.getCargo(), parsedMobi.getCargo());
        assertEquals(Event.DATA_RECEIVED, parsedMobi.getEvent());
    }

    @Test
    public void testCgmStateChange_cycleStartThenNoData() throws DecoderException {
        // Maintainer's t:slim X2 (2024), Dexcom G7 session, a cycle with no reading. Header high nibble 0.
        CgmStateChangeGxHistoryLog expectedStart = new CgmStateChangeGxHistoryLog(
                534170224L, 1048758L, 2, 2, 1
        );
        CgmStateChangeGxHistoryLog parsedStart = (CgmStateChangeGxHistoryLog) HistoryLogMessageTester.testSingle(
                "db0070cad61fb600100002000000020000000000000001000000",
                expectedStart
        );
        assertHexEquals(expectedStart.getCargo(), parsedStart.getCargo());

        CgmStateChangeGxHistoryLog expectedNoData = new CgmStateChangeGxHistoryLog(
                534170251L, 1048761L, 2, 2, 3
        );
        CgmStateChangeGxHistoryLog parsedNoData = (CgmStateChangeGxHistoryLog) HistoryLogMessageTester.testSingle(
                "db008bcad61fb900100002000000020000000000000003000000",
                expectedNoData
        );
        assertHexEquals(expectedNoData.getCargo(), parsedNoData.getCargo());
        assertEquals(Event.NO_DATA, parsedNoData.getEvent());
        assertEquals(State.ACTIVE, parsedNoData.getState());
        assertFalse(parsedNoData.isStateChange());
        assertEquals(27, parsedNoData.getPumpTimeSec() - parsedStart.getPumpTimeSec());
    }

    @Test
    public void testCgmStateChange_sessionStopAndRestart() throws DecoderException {
        // Maintainer's t:slim X2 (2024), Dexcom G7 sensor change. Header high nibble 0.
        // Stop: same second as the CgmStopSessionG7 record.
        CgmStateChangeGxHistoryLog expectedStop = new CgmStateChangeGxHistoryLog(
                534413823L, 1061550L, 2, 0, 4
        );
        CgmStateChangeGxHistoryLog parsedStop = (CgmStateChangeGxHistoryLog) HistoryLogMessageTester.testSingle(
                "db00ff81da1fae32100002000000000000000000000004000000",
                expectedStop
        );
        assertHexEquals(expectedStop.getCargo(), parsedStop.getCargo());
        assertEquals(State.ACTIVE, parsedStop.getPreviousState());
        assertEquals(State.STOPPED, parsedStop.getState());
        assertEquals(Event.SESSION_COMMAND, parsedStop.getEvent());
        assertTrue(parsedStop.isStateChange());

        // Start: same second as the CgmStartSensorReqG7 record.
        CgmStateChangeGxHistoryLog expectedStart = new CgmStateChangeGxHistoryLog(
                534413964L, 1061577L, 0, 1, 1
        );
        CgmStateChangeGxHistoryLog parsedStart = (CgmStateChangeGxHistoryLog) HistoryLogMessageTester.testSingle(
                "db008c82da1fc932100000000000010000000000000001000000",
                expectedStart
        );
        assertHexEquals(expectedStart.getCargo(), parsedStart.getCargo());
        assertEquals(State.STOPPED, parsedStart.getPreviousState());
        assertEquals(State.START_PENDING, parsedStart.getState());
        assertEquals(Event.CYCLE_START, parsedStart.getEvent());

        // Active again: same second as the CgmJoinSessionG7 record.
        CgmStateChangeGxHistoryLog expectedActive = new CgmStateChangeGxHistoryLog(
                534414060L, 1061589L, 1, 2, 2
        );
        CgmStateChangeGxHistoryLog parsedActive = (CgmStateChangeGxHistoryLog) HistoryLogMessageTester.testSingle(
                "db00ec82da1fd532100001000000020000000000000002000000",
                expectedActive
        );
        assertHexEquals(expectedActive.getCargo(), parsedActive.getCargo());
        assertEquals(State.START_PENDING, parsedActive.getPreviousState());
        assertEquals(State.ACTIVE, parsedActive.getState());
        assertEquals(Event.DATA_RECEIVED, parsedActive.getEvent());
    }

    @Test
    public void testCgmStateChange_userActivity() throws DecoderException {
        // Maintainer's t:slim X2 (2024), written right after a PumpButton release. Header high nibble 0.
        CgmStateChangeGxHistoryLog expected = new CgmStateChangeGxHistoryLog(
                532612484L, 976447L, 2, 2, 5
        );

        CgmStateChangeGxHistoryLog parsedRes = (CgmStateChangeGxHistoryLog) HistoryLogMessageTester.testSingle(
                "db008405bf1f3fe60e0002000000020000000000000005000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(State.ACTIVE, parsedRes.getState());
        assertEquals(Event.USER_ACTIVITY, parsedRes.getEvent());
    }

    @Test
    public void testCgmStateChange_unidentifiedIdsMapToUnknown() {
        CgmStateChangeGxHistoryLog log = new CgmStateChangeGxHistoryLog(0L, 0L, 1, 3, 9);
        assertEquals(State.START_PENDING, log.getPreviousState());
        assertEquals(3, log.getStateId());
        assertEquals(State.UNKNOWN, log.getState());
        assertEquals(9, log.getEventId());
        assertEquals(Event.UNKNOWN, log.getEvent());
    }
}
