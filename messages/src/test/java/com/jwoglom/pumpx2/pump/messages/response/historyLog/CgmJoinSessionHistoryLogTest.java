package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmJoinSessionHistoryLogTest {
    // Observed on a t:slim X2 newly paired to a Dexcom G6 that was already in session (official app BLE capture).
    // currentTransmitterTime - sessionStartTime = 317460 s matches the record's pump time minus the
    // CgmStatusV2 sensorStartedTimestamp (317459 s); CgmStatusV2 reported sessionDurationSeconds = 864000 (10 days).
    @Test
    public void testCgmJoinSessionHistoryLogTransmitterInSession() throws DecoderException {
        CgmJoinSessionHistoryLog expected = new CgmJoinSessionHistoryLog(
                // long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, int sessionJoinReasonRaw, int sessionDuration
                503758995L, 2107936L, 3125188L, 2807728L, 10, 10
        );

        CgmJoinSessionHistoryLog parsedRes = (CgmJoinSessionHistoryLog) HistoryLogMessageTester.testSingle(
                "d50093c0061e202a2000c4af2f00b0d72a00000000000a0a0000",
                expected
        );
        assertEquals(503758995L, parsedRes.getPumpTimeSec());
        assertEquals(2107936L, parsedRes.getSequenceNum());
        assertEquals(3125188L, parsedRes.getCurrentTransmitterTime());
        assertEquals(2807728L, parsedRes.getSessionStartTime());
        assertEquals(10, parsedRes.getSessionJoinReasonRaw());
        assertEquals(CgmJoinSessionHistoryLog.SessionJoinReason.DEXBLES_REASON_TRANSMITTER_IN_SESSION, parsedRes.getSessionJoinReason());
        assertEquals(10, parsedRes.getSessionDuration());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCgmJoinSessionHistoryLogBuildCargoLayout() {
        CgmJoinSessionHistoryLog log = new CgmJoinSessionHistoryLog(1L, 2L, 3L, 4L, 6, 10);
        assertEquals(10, log.getCargo()[22]);
        assertEquals(6, log.getCargo()[23]);
        assertEquals(0, log.getCargo()[24]);
        assertEquals(0, log.getCargo()[25]);
    }
}
