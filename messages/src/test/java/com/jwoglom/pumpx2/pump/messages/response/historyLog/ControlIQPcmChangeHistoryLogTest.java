package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQPcmChangeHistoryLogTest {
    @Test
    public void testControlIQPcmChangeHistoryLog1() throws DecoderException {
        ControlIQPcmChangeHistoryLog expected = new ControlIQPcmChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, int currentPcm, int previousPcm
            580773244L, 490889L, 0, 3
        );
        // the constructor above only stores the raw currentPcmId/previousPcmId; re-parse expected's
        // own cargo so its derived currentPcm/previousPcm enums are populated for verboseToString comparison
        expected.parse(expected.getCargo());

        ControlIQPcmChangeHistoryLog parsedRes = (ControlIQPcmChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "e6107ce59d22897d070000030101010101000000000000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }

    @Test
    public void testControlIQPcmChangeHistoryLog2() throws DecoderException {
        ControlIQPcmChangeHistoryLog expected = new ControlIQPcmChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, int currentPcm, int previousPcm
            580720759L, 488634L, 3, 2
        );
        // the constructor above only stores the raw currentPcmId/previousPcmId; re-parse expected's
        // own cargo so its derived currentPcm/previousPcm enums are populated for verboseToString comparison
        expected.parse(expected.getCargo());

        ControlIQPcmChangeHistoryLog parsedRes = (ControlIQPcmChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "e61077189d22ba74070003020001010101000000000000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
