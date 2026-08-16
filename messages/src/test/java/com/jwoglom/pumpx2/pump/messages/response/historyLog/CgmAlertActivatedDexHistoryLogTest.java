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

        // no cargo round-trip: buildCargo hardcodes byte 1's low nibble to 0, but typeId 369
        // needs 1 there (bits 8-11 of the 12-bit typeId), independent of the header high nibble;
        // bytes 14-15, 18, and 24-25 also carry unparsed nonzero data that buildCargo zero-fills
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

        // no cargo round-trip: buildCargo hardcodes byte 1's low nibble to 0, but typeId 369
        // needs 1 there (bits 8-11 of the 12-bit typeId), independent of the header high nibble;
        // bytes 14-15, 18, and 23-25 also carry unparsed nonzero data that buildCargo zero-fills
        HistoryLogMessageTester.testSingle(
                "71114b179d22a77407000e0300000e2100001900000000406544",
                expected
        );
    }
}
