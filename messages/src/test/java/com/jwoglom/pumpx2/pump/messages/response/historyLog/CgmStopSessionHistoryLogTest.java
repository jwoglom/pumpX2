package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmStopSessionHistoryLogTest {
    // Observed on a Tandem Mobi with a Dexcom G6 (official app BLE captures).

    // 34m59s after the CgmStartSession record (seq 52248) on the same pump; currentTransmitterTime
    // advanced by 2098 s over the same interval.
    @Test
    public void testCgmStopSessionHistoryLogTransmitterNotInSession() throws DecoderException {
        CgmStopSessionHistoryLog expected = (CgmStopSessionHistoryLog) new CgmStopSessionHistoryLog(
                // long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int sessionStopReasonRaw, int sessionDuration
                540616480L, 52337L, 2120781L, 4294967295L, 0L, 6, 10
        ).withHeaderHighNibble(1);

        CgmStopSessionHistoryLog parsedRes = (CgmStopSessionHistoryLog) HistoryLogMessageTester.testSingle(
                "d6102027392071cc00004d5c2000ffffffff000000000a060000",
                expected
        );
        assertEquals(540616480L, parsedRes.getPumpTimeSec());
        assertEquals(52337L, parsedRes.getSequenceNum());
        assertEquals(2120781L, parsedRes.getCurrentTransmitterTime());
        assertEquals(4294967295L, parsedRes.getSessionStartTime());
        assertEquals(0L, parsedRes.getSessionStopTime());
        assertEquals(6, parsedRes.getSessionStopReasonRaw());
        assertEquals(CgmStopSessionHistoryLog.SessionStopReason.DEXBLES_REASON_TRANSMITTER_NOT_IN_SESSION, parsedRes.getSessionStopReason());
        assertEquals(10, parsedRes.getSessionDuration());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // Same capture as the app's StopDexcomCGMSensorSessionRequest, after which CgmStatusV2 reported STATUS_STOPPED.
    @Test
    public void testCgmStopSessionHistoryLogUserStop() throws DecoderException {
        CgmStopSessionHistoryLog expected = (CgmStopSessionHistoryLog) new CgmStopSessionHistoryLog(
                // long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, long sessionStopTime, int sessionStopReasonRaw, int sessionDuration
                536004396L, 48530L, 0L, 4294967295L, 0L, 0, 10
        ).withHeaderHighNibble(1);

        CgmStopSessionHistoryLog parsedRes = (CgmStopSessionHistoryLog) HistoryLogMessageTester.testSingle(
                "d6102cc7f21f92bd000000000000ffffffff000000000a000000",
                expected
        );
        assertEquals(0, parsedRes.getSessionStopReasonRaw());
        assertEquals(CgmStopSessionHistoryLog.SessionStopReason.DEXBLES_REASON_USER, parsedRes.getSessionStopReason());
        assertEquals(10, parsedRes.getSessionDuration());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
