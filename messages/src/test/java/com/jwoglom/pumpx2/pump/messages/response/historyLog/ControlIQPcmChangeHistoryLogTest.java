package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQPcmChangeHistoryLogTest {
    @Test
    public void testControlIQPcmChangeHistoryLog1() throws DecoderException {
        ControlIQPcmChangeHistoryLog expected = (ControlIQPcmChangeHistoryLog) new ControlIQPcmChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, int currentPcm, int previousPcm,
            // int pumpSuspended, int calculationAvailable, int cgmAvailable,
            // int closedLoopPreferred, int sufficientClosedLoopParams
            580773244L, 490889L, 0, 3, 1, 1, 1, 1, 1
        ).withHeaderHighNibble(1);

        ControlIQPcmChangeHistoryLog parsedRes = (ControlIQPcmChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "e6107ce59d22897d070000030101010101000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testControlIQPcmChangeHistoryLog2() throws DecoderException {
        ControlIQPcmChangeHistoryLog expected = (ControlIQPcmChangeHistoryLog) new ControlIQPcmChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, int currentPcm, int previousPcm,
            // int pumpSuspended, int calculationAvailable, int cgmAvailable,
            // int closedLoopPreferred, int sufficientClosedLoopParams
            580720759L, 488634L, 3, 2, 0, 1, 1, 1, 1
        ).withHeaderHighNibble(1);

        ControlIQPcmChangeHistoryLog parsedRes = (ControlIQPcmChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "e61077189d22ba74070003020001010101000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
