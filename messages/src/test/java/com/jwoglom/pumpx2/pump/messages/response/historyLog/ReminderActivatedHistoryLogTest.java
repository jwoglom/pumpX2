package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ReminderActivatedHistoryLogTest {
    @Test
    public void testReminderActivatedHistoryLog1() throws DecoderException {
        ReminderActivatedHistoryLog expected = new ReminderActivatedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long reminderId
            580777206L, 491057L, 2
        );

        // Bytes 14-25 of this capture are a nonzero, unparsed tail (b0200000000000000080ac44)
        // that is identical across all captured type-25 records; parse() does not read it.
        HistoryLogMessageTester.testSingle(
                "1910f6f49d22317e070002000000b0200000000000000080ac44",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }

    @Test
    public void testReminderActivatedHistoryLog2() throws DecoderException {
        ReminderActivatedHistoryLog expected = new ReminderActivatedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long reminderId
            579740400L, 447851L, 2
        );

        // Bytes 14-25 of this capture are a nonzero, unparsed tail (b0200000000000000080ac44)
        // that is identical across all captured type-25 records; parse() does not read it.
        HistoryLogMessageTester.testSingle(
                "1910f0228e226bd5060002000000b0200000000000000080ac44",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
