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

    @Test
    public void testBolusCompletedHistoryLog_mismatchingFloats() throws DecoderException {
        // BolusCompletedHistoryLog[bolusId=1047,completionStatus=3,insulinDelivered=0.89000005,insulinRequested=0.89,iob=1.0332867,cargo={20,0,0,91,-105,26,-7,-45,2,0,3,0,23,4,-67,66,-124,63,11,-41,99,63,10,-41,99,63},pumpTimeSec=446126848,sequenceNum=185337]
        BolusCompletedHistoryLog expected = new BolusCompletedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested
                446126848L, 185337L, 3, 1047, 1.0332867F, 0.89000005F, 0.89F
        );

        BolusCompletedHistoryLog parsedRes = (BolusCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "1400005b971af9d3020003001704bd42843f0bd7633f0ad7633f",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-19T12:07:28Z"), parsedRes.getPumpTimeSecInstant());
    }
}