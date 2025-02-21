package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import java.util.Arrays;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;
@Ignore("needs historyLog sample")
public class DateChangeHistoryLogTest {
    @Test
    public void testDateChangeHistoryLog() throws DecoderException {
        // datePrior: 5185 days after Jan 1, 2008 - March 13, 2022
        // dateAfter: 5280 days after Jan 1, 2008 - June 16, 2022

        DateChangeHistoryLog expected = new DateChangeHistoryLog(
            // long pumpTimeSec, long sequenceNum, long datePrior, long dateAfter, long rawRTCTime
            456260319L, 2958L, 5185L, 5280L, 907212443L
        );

        DateChangeHistoryLog parsedRes = (DateChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "0e00dffa311b8e0b000041140000a01400009bf6123600000000",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-03-13T00:00:00.00Z"), parsedRes.getDatePriorInstant());
        assertEquals(Instant.parse("2022-06-16T00:00:00.00Z"), parsedRes.getDateAfterInstant());

        // The pumpTimeSec reflects the date change
        assertEquals(Instant.parse("2022-06-16T18:58:39.00Z"), parsedRes.getPumpTimeSecInstant());
    }
}