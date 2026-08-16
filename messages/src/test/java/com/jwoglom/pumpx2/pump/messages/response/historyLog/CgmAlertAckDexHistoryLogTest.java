package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmAlertAckDexHistoryLogTest {
    @Test
    public void testCgmAlertAckDexHistoryLog1() throws DecoderException {
        CgmAlertAckDexHistoryLog expected = new CgmAlertAckDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580750504L, 489859L, 770L
        );

        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
        HistoryLogMessageTester.testSingle(
                "7311a88c9d228379070002030000000000000000000000000000",
                expected
        );
    }

    @Test
    public void testCgmAlertAckDexHistoryLog2() throws DecoderException {
        CgmAlertAckDexHistoryLog expected = new CgmAlertAckDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            579789268L, 449784L, 800L
        );

        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
        // this sample also has a nonzero byte at payload offset 4 (raw byte 14 = 0x01) which
        // CgmAlertAckDexHistoryLog.parse() does not read; see report for details
        HistoryLogMessageTester.testSingle(
                "7311d4e18e22f8dc060020030000010000000000000000000000",
                expected
        );
    }
}
