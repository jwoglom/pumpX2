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

        // no cargo round-trip: buildCargo hardcodes byte 1's low nibble to 0, but typeId 371
        // needs 1 there (bits 8-11 of the 12-bit typeId), independent of the header high nibble
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

        // no cargo round-trip: buildCargo hardcodes byte 1's low nibble to 0, but typeId 371
        // needs 1 there (bits 8-11 of the 12-bit typeId), independent of the header high nibble;
        // this sample also has a nonzero byte at payload offset 4 (raw byte 14 = 0x01) which
        // CgmAlertAckDexHistoryLog.parse() does not read; see report for details
        HistoryLogMessageTester.testSingle(
                "7311d4e18e22f8dc060020030000010000000000000000000000",
                expected
        );
    }
}
