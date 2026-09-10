package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.response.historyLog.PumpingSuspendedHistoryLogTest.withoutSourceNibble;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpingResumedHistoryLogTest {
    @Test
    public void testPumpingResumedHistoryLog() throws DecoderException {
        PumpingResumedHistoryLog expected = new PumpingResumedHistoryLog(
            // long preResumeState, int insulinAmount
                100, 180
        );

        PumpingResumedHistoryLog parsedRes = (PumpingResumedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "0c005fea951aa4c9020064000000b40000000000000000000000",
                expected
        );
        assertEquals(100L, parsedRes.getPreResumeState());
        assertEquals(180, parsedRes.getInsulinAmount());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testPumpingResumedHistoryLogAfterAlarmSuspend() throws DecoderException {
        PumpingResumedHistoryLog expected = new PumpingResumedHistoryLog(
                // long pumpTimeSec, long sequenceNum, long preResumeState, int insulinAmount
                587315910L, 744340L, 100, 150
        );

        PumpingResumedHistoryLog parsedRes = (PumpingResumedHistoryLog) HistoryLogMessageTester.testSingle(
                "0c10c6ba0123945b0b0064000000960000000000000000000000",
                expected
        );
        assertEquals(587315910L, parsedRes.getPumpTimeSec());
        assertEquals(744340L, parsedRes.getSequenceNum());
        assertEquals(100L, parsedRes.getPreResumeState());
        assertEquals(150, parsedRes.getInsulinAmount());
        assertHexEquals(expected.getCargo(), withoutSourceNibble(parsedRes.getCargo()));
    }

    @Test
    public void testPumpingResumedHistoryLogAfterUserSuspend() throws DecoderException {
        PumpingResumedHistoryLog expected = new PumpingResumedHistoryLog(
                // long pumpTimeSec, long sequenceNum, long preResumeState, int insulinAmount
                587388971L, 748806L, 100, 105
        );

        PumpingResumedHistoryLog parsedRes = (PumpingResumedHistoryLog) HistoryLogMessageTester.testSingle(
                "0c102bd80223066d0b0064000000690000000000000000000000",
                expected
        );
        assertEquals(587388971L, parsedRes.getPumpTimeSec());
        assertEquals(748806L, parsedRes.getSequenceNum());
        assertEquals(100L, parsedRes.getPreResumeState());
        assertEquals(105, parsedRes.getInsulinAmount());
        assertHexEquals(expected.getCargo(), withoutSourceNibble(parsedRes.getCargo()));
    }

    // The two tests below parse records observed on a Tandem Mobi driven by Trio (Control-IQ off,
    // no CGM paired), Sept 2026 BLE capture. In both, insulinAmount matched
    // InsulinStatusResponse.currentInsulinAmount polled within two seconds of the resume.

    /**
     * Resume following a cartridge change: the paired PumpingSuspended record read 8 units, and
     * CartridgeFilledHistoryLog.insulinDisplay logged in the same second was 185. insulinAmount
     * is therefore the remaining reservoir amount rather than a value carried over from suspend.
     */
    @Test
    public void testPumpingResumedHistoryLogAfterCartridgeChangeMobi() throws DecoderException {
        PumpingResumedHistoryLog expected = new PumpingResumedHistoryLog(
                // long pumpTimeSec, long sequenceNum, long preResumeState, int insulinAmount
                589745836L, 651756L, 100, 185
        );

        PumpingResumedHistoryLog parsedRes = (PumpingResumedHistoryLog) HistoryLogMessageTester.testSingle(
                "0c10acce2623ecf1090064000000b90000000000000000000000",
                expected
        );
        assertEquals(589745836L, parsedRes.getPumpTimeSec());
        assertEquals(651756L, parsedRes.getSequenceNum());
        assertEquals(100L, parsedRes.getPreResumeState());
        assertEquals(185, parsedRes.getInsulinAmount());
        assertHexEquals(expected.getCargo(), withoutSourceNibble(parsedRes.getCargo()));
    }

    /**
     * Resume following a tubing fill on the same cartridge: the paired PumpingSuspended record
     * read 110 units and the live InsulinStatusResponse dropped 110 -> 100 -> 90 during the prime,
     * so the resume records the post-prime remaining amount of 90.
     */
    @Test
    public void testPumpingResumedHistoryLogAfterTubingFillMobi() throws DecoderException {
        PumpingResumedHistoryLog expected = new PumpingResumedHistoryLog(
                // long pumpTimeSec, long sequenceNum, long preResumeState, int insulinAmount
                589585424L, 643715L, 100, 90
        );

        PumpingResumedHistoryLog parsedRes = (PumpingResumedHistoryLog) HistoryLogMessageTester.testSingle(
                "0c10105c242383d20900640000005a0000000000000000000000",
                expected
        );
        assertEquals(589585424L, parsedRes.getPumpTimeSec());
        assertEquals(643715L, parsedRes.getSequenceNum());
        assertEquals(100L, parsedRes.getPreResumeState());
        assertEquals(90, parsedRes.getInsulinAmount());
        assertHexEquals(expected.getCargo(), withoutSourceNibble(parsedRes.getCargo()));
    }
}
