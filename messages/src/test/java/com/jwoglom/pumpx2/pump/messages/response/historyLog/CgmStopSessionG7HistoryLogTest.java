package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmStopSessionG7HistoryLogTest {
    @Test
    public void testCgmStopSessionG7HistoryLogParse() {
        CgmStopSessionG7HistoryLog expected = new CgmStopSessionG7HistoryLog(
                // long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int stopSessionCode, int sessionStopReason, int sessionDuration
                566516808L, 506706L, 566516805L, 566430000L, 566516800L, 1, 2, 10
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmStopSessionG7HistoryLog);

        CgmStopSessionG7HistoryLog parsedRes = (CgmStopSessionG7HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(expected.getCurrentTransmitterTime(), parsedRes.getCurrentTransmitterTime());
        assertEquals(expected.getSessionStartTime(), parsedRes.getSessionStartTime());
        assertEquals(expected.getSessionStopTime(), parsedRes.getSessionStopTime());
        assertEquals(expected.getStopSessionCode(), parsedRes.getStopSessionCode());
        assertEquals(expected.getSessionStopReason(), parsedRes.getSessionStopReason());
        assertEquals(expected.getSessionDuration(), parsedRes.getSessionDuration());
        assertEquals(10, parsedRes.getCargo()[22]);
        assertEquals(2, parsedRes.getCargo()[23]);
        assertEquals(1, parsedRes.getCargo()[24]);
        assertEquals(0, parsedRes.getCargo()[25]);
    }

    // Observed on a Tandem Mobi with a Dexcom G7 (official app BLE capture): written when a new sensor code was
    // entered, after the app's StopDexcomCGMSensorSessionRequest had left CgmStatusV2 in SESSION_STOP_PENDING.
    // currentTransmitterTime 905914 s is inside the 12 h grace period after the 10-day session.
    @Test
    public void testCgmStopSessionG7HistoryLogUserStopMobi() throws DecoderException {
        CgmStopSessionG7HistoryLog expected = (CgmStopSessionG7HistoryLog) new CgmStopSessionG7HistoryLog(
                // long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int stopSessionCode, int sessionStopReason, int sessionDuration
                542807916L, 231564L, 905914L, 91L, 0L, 0, 16, 10
        ).withHeaderHighNibble(1);

        CgmStopSessionG7HistoryLog parsedRes = (CgmStopSessionG7HistoryLog) HistoryLogMessageTester.testSingle(
                "bf116c975a208c880300bad20d005b000000000000000a100000",
                expected
        );
        assertEquals(542807916L, parsedRes.getPumpTimeSec());
        assertEquals(231564L, parsedRes.getSequenceNum());
        assertEquals(905914L, parsedRes.getCurrentTransmitterTime());
        assertEquals(91L, parsedRes.getSessionStartTime());
        assertEquals(0L, parsedRes.getSessionStopTime());
        assertEquals(10, parsedRes.getSessionDuration());
        assertEquals(16, parsedRes.getSessionStopReason());
        assertEquals(0, parsedRes.getStopSessionCode());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // Observed on a t:slim X2 with a Dexcom G7 (official app BLE capture): no stop request preceded it, and
    // currentTransmitterTime 907415 s is past the 10-day session plus 12 h grace (907200 s).
    @Test
    public void testCgmStopSessionG7HistoryLogExpiredX2() throws DecoderException {
        CgmStopSessionG7HistoryLog expected = new CgmStopSessionG7HistoryLog(
                // long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int stopSessionCode, int sessionStopReason, int sessionDuration
                534413823L, 1061554L, 907415L, 4294967295L, 0L, 0, 14, 10
        );

        CgmStopSessionG7HistoryLog parsedRes = (CgmStopSessionG7HistoryLog) HistoryLogMessageTester.testSingle(
                "bf01ff81da1fb232100097d80d00ffffffff000000000a0e0000",
                expected
        );
        assertEquals(907415L, parsedRes.getCurrentTransmitterTime());
        assertEquals(4294967295L, parsedRes.getSessionStartTime());
        assertEquals(10, parsedRes.getSessionDuration());
        assertEquals(14, parsedRes.getSessionStopReason());
        assertEquals(0, parsedRes.getStopSessionCode());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
