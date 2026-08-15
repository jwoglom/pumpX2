package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmAlertActivatedDexHistoryLogTest {
    @Test
    public void testCgmAlertActivatedDexHistoryLog1() throws DecoderException {
        CgmAlertActivatedDexHistoryLog expected = new CgmAlertActivatedDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580770552L, 490757L, 770L
        );

        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
        HistoryLogMessageTester.testSingle(
                "7111f8da9d22057d07000203000014210000d000000000004843",
                expected
        );
    }

    @Test
    public void testCgmAlertActivatedDexHistoryLog2() throws DecoderException {
        CgmAlertActivatedDexHistoryLog expected = new CgmAlertActivatedDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580720459L, 488615L, 782L
        );

        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
        HistoryLogMessageTester.testSingle(
                "71114b179d22a77407000e0300000e2100001900000000406544",
                expected
        );
    }
}
