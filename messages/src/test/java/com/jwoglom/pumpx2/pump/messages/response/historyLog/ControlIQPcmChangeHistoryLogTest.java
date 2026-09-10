package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    // Observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM paired), Sept 2026 BLE capture.
    // One PCM change record is written on each suspend and each resume (8 records, 4 pairs):
    // currentPcm/previousPcm swap between NO_CONTROL (0) and OPEN_LOOP (1), pumpSuspended@12 flips
    // with the suspend state, cgmAvailable@14 is 0 throughout (no CGM on the pump),
    // closedLoopPreferred@15 is 0 (Control-IQ off), and all five booleans are strictly 0/1.
    @Test
    public void testControlIQPcmChangeHistoryLog_MobiSuspend() throws DecoderException {
        ControlIQPcmChangeHistoryLog expected = (ControlIQPcmChangeHistoryLog) new ControlIQPcmChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, int currentPcm, int previousPcm,
            // int pumpSuspended, int calculationAvailable, int cgmAvailable,
            // int closedLoopPreferred, int sufficientClosedLoopParams
            589553316L, 641937L, 0, 1, 1, 1, 0, 0, 1
        ).withHeaderHighNibble(1);

        ControlIQPcmChangeHistoryLog parsedRes = (ControlIQPcmChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "e610a4de232391cb090000010101000001000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(589553316L, parsedRes.getPumpTimeSec());
        assertEquals(641937L, parsedRes.getSequenceNum());
        assertEquals(ControlIQPcmChangeHistoryLog.PCM.NO_CONTROL, parsedRes.getCurrentPcm());
        assertEquals(ControlIQPcmChangeHistoryLog.PCM.OPEN_LOOP, parsedRes.getPreviousPcm());
        assertEquals(0, parsedRes.getCurrentPcmId());
        assertEquals(1, parsedRes.getPreviousPcmId());
        assertTrue(parsedRes.isPumpSuspended());
        assertTrue(parsedRes.isCalculationAvailable());
        assertFalse(parsedRes.isCgmAvailable());
        assertFalse(parsedRes.isClosedLoopPreferred());
        assertTrue(parsedRes.isSufficientClosedLoopParams());
        assertEquals(1, parsedRes.getPumpSuspendedRaw());
        assertEquals(1, parsedRes.getCalculationAvailableRaw());
        assertEquals(0, parsedRes.getCgmAvailableRaw());
        assertEquals(0, parsedRes.getClosedLoopPreferredRaw());
        assertEquals(1, parsedRes.getSufficientClosedLoopParamsRaw());
    }

    // Observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM paired), Sept 2026 BLE capture.
    // The resume paired with the suspend above: the PCM values swap back and pumpSuspended clears.
    @Test
    public void testControlIQPcmChangeHistoryLog_MobiResume() throws DecoderException {
        ControlIQPcmChangeHistoryLog expected = (ControlIQPcmChangeHistoryLog) new ControlIQPcmChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, int currentPcm, int previousPcm,
            // int pumpSuspended, int calculationAvailable, int cgmAvailable,
            // int closedLoopPreferred, int sufficientClosedLoopParams
            589555699L, 642015L, 1, 0, 0, 1, 0, 0, 1
        ).withHeaderHighNibble(1);

        ControlIQPcmChangeHistoryLog parsedRes = (ControlIQPcmChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "e610f3e72323dfcb090001000001000001000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(589555699L, parsedRes.getPumpTimeSec());
        assertEquals(642015L, parsedRes.getSequenceNum());
        assertEquals(ControlIQPcmChangeHistoryLog.PCM.OPEN_LOOP, parsedRes.getCurrentPcm());
        assertEquals(ControlIQPcmChangeHistoryLog.PCM.NO_CONTROL, parsedRes.getPreviousPcm());
        assertEquals(1, parsedRes.getCurrentPcmId());
        assertEquals(0, parsedRes.getPreviousPcmId());
        assertFalse(parsedRes.isPumpSuspended());
        assertTrue(parsedRes.isCalculationAvailable());
        assertFalse(parsedRes.isCgmAvailable());
        assertFalse(parsedRes.isClosedLoopPreferred());
        assertTrue(parsedRes.isSufficientClosedLoopParams());
        assertEquals(0, parsedRes.getPumpSuspendedRaw());
        assertEquals(1, parsedRes.getCalculationAvailableRaw());
        assertEquals(0, parsedRes.getCgmAvailableRaw());
        assertEquals(0, parsedRes.getClosedLoopPreferredRaw());
        assertEquals(1, parsedRes.getSufficientClosedLoopParamsRaw());
    }
}
