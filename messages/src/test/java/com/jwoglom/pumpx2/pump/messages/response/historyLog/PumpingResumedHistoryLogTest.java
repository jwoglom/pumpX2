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
}
