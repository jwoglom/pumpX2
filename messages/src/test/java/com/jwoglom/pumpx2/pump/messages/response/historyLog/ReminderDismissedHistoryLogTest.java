package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ReminderDismissedHistoryLogTest {
    @Test
    public void testReminderDismissedHistoryLog1() throws DecoderException {
        ReminderDismissedHistoryLog expected = new ReminderDismissedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long reminderId
            580095338L, 462904L, 2
        );

        // Bytes 14-25 of this capture are a nonzero, unparsed tail (91d19d175f00000064050000)
        // that parse() does not read; it differs from the other captured type-29 record below
        // except for a constant trailing 64050000.
        HistoryLogMessageTester.testSingle(
                "1d106a8d9322381007000200000091d19d175f00000064050000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }

    @Test
    public void testReminderDismissedHistoryLog2() throws DecoderException {
        ReminderDismissedHistoryLog expected = new ReminderDismissedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long reminderId
            579744845L, 448171L, 2
        );

        // Bytes 14-25 of this capture are a nonzero, unparsed tail (56b8b9020e00000064050000)
        // that parse() does not read; it differs from the other captured type-29 record above
        // except for a constant trailing 64050000.
        HistoryLogMessageTester.testSingle(
                "1d104d348e22abd606000200000056b8b9020e00000064050000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
