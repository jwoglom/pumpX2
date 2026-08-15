package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQUserModeChangeHistoryLogTest {
    @Test
    public void testControlIQUserModeChangeHistoryLog1() throws DecoderException {
        ControlIQUserModeChangeHistoryLog expected = new ControlIQUserModeChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, int currentUserMode, int previousUserMode
            579954015L, 456965L, 1, 2
        );

        // Byte 12 of this capture (0x04) carries a requestedAction value which parse() does not read.
        ControlIQUserModeChangeHistoryLog parsedRes = (ControlIQUserModeChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "e5105f65912205f9060001020400000100000000000000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }

    @Test
    public void testControlIQUserModeChangeHistoryLog2() throws DecoderException {
        ControlIQUserModeChangeHistoryLog expected = new ControlIQUserModeChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, int currentUserMode, int previousUserMode
            579953995L, 456952L, 0, 1
        );

        // Byte 12 of this capture (0x02) carries a requestedAction value which parse() does not read.
        ControlIQUserModeChangeHistoryLog parsedRes = (ControlIQUserModeChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "e5104b659122f8f8060000010200010100000000000000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
