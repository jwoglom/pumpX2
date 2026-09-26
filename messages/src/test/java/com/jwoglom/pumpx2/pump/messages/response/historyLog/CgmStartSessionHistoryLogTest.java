package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmStartSessionHistoryLogTest {
    // Observed on a Tandem Mobi with a Dexcom G6 (official app BLE capture): written a few seconds after the
    // app's StartDexcomG6SensorSessionRequest, as CgmStatusV2 moved to START_PENDING. G6 sessions last 10 days.
    @Test
    public void testCgmStartSessionHistoryLogG6Start() throws DecoderException {
        CgmStartSessionHistoryLog expected = (CgmStartSessionHistoryLog) new CgmStartSessionHistoryLog(
                // long pumpTimeSec, long sequenceNum, long currentTransmitterTime, long sessionStartTime, int sessionDuration
                540614381L, 52248L, 2118683L, 2118683L, 10
        ).withHeaderHighNibble(1);

        CgmStartSessionHistoryLog parsedRes = (CgmStartSessionHistoryLog) HistoryLogMessageTester.testSingle(
                "d410ed1e392018cc00001b5420001b542000000000000a000000",
                expected
        );
        assertEquals(540614381L, parsedRes.getPumpTimeSec());
        assertEquals(52248L, parsedRes.getSequenceNum());
        assertEquals(2118683L, parsedRes.getCurrentTransmitterTime());
        assertEquals(2118683L, parsedRes.getSessionStartTime());
        assertEquals(10, parsedRes.getSessionDuration());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
