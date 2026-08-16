package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmAlertClearedDexHistoryLogTest {
    @Test
    public void testCgmAlertClearedDexHistoryLog1() throws DecoderException {
        CgmAlertClearedDexHistoryLog expected = new CgmAlertClearedDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580777763L, 491134L, 770L
        );

        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
        HistoryLogMessageTester.testSingle(
                "721123f79d227e7e070002030000000000000000000000000000",
                expected
        );
    }

    @Test
    public void testCgmAlertClearedDexHistoryLog2() throws DecoderException {
        CgmAlertClearedDexHistoryLog expected = new CgmAlertClearedDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580720754L, 488623L, 782L
        );

        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
        HistoryLogMessageTester.testSingle(
                "721172189d22af7407000e030000000000000000000000000000",
                expected
        );
    }
}
