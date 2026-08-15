package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ReminderDismissedHistoryLogTest {
    // ReminderDismissedHistoryLog (typeId 29) is not registered in
    // HistoryLogParser.LOG_MESSAGE_TYPES, so HistoryLogParser.parse() currently returns an
    // UnknownHistoryLog for real captures of this type instead of dispatching here. That is a
    // pre-existing gap outside this batch's scope (no production code is modified by these
    // tests), so these tests call ReminderDismissedHistoryLog#parse directly to verify its own
    // decoding logic against a real capture.
    @Test
    public void testReminderDismissedHistoryLog1() throws DecoderException {
        ReminderDismissedHistoryLog expected = new ReminderDismissedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long reminderId
            580095338L, 462904L, 2
        );

        // Bytes 14-25 of this capture are a nonzero, unparsed tail (91d19d175f00000064050000)
        // that parse() does not read; it differs from the other captured type-29 record below
        // except for a constant trailing 64050000.
        ReminderDismissedHistoryLog parsedRes = new ReminderDismissedHistoryLog();
        parsedRes.parse(Hex.decodeHex("1d106a8d9322381007000200000091d19d175f00000064050000"));

        assertEquals(expected.verboseToString(), parsedRes.verboseToString());
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
        ReminderDismissedHistoryLog parsedRes = new ReminderDismissedHistoryLog();
        parsedRes.parse(Hex.decodeHex("1d104d348e22abd606000200000056b8b9020e00000064050000"));

        assertEquals(expected.verboseToString(), parsedRes.verboseToString());
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
