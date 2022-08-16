package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.time.Instant;

public class BolusCompletedHistoryLogTest {
    @Test
    public void testBolusCompletedHistoryLog() throws DecoderException {
        BolusCompletedHistoryLog expected = new BolusCompletedHistoryLog(
            // long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested
            446158750L, 186480L, 3, 1057, 3.652852F, 1.7869551F, 1.7869551F
        );

        BolusCompletedHistoryLog parsedRes = (BolusCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "14009ed7971a70d802000300210454c86940f2bae43ff2bae43f",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-19T20:59:10Z"), parsedRes.getPumpTimeSecInstant());
    }
}