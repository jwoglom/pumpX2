package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ReminderActivatedHistoryLogTest {
    // ReminderActivatedHistoryLog (typeId 25) is not registered in
    // HistoryLogParser.LOG_MESSAGE_TYPES, so HistoryLogParser.parse() currently returns an
    // UnknownHistoryLog for real captures of this type instead of dispatching here. That is a
    // pre-existing gap outside this batch's scope (no production code is modified by these
    // tests), so these tests call ReminderActivatedHistoryLog#parse directly to verify its own
    // decoding logic against a real capture.
    @Test
    public void testReminderActivatedHistoryLog1() throws DecoderException {
        ReminderActivatedHistoryLog expected = new ReminderActivatedHistoryLog(
            // long pumpTimeSec, long sequenceNum, long reminderId
            580777206L, 491057L, 2
        );

        // Bytes 14-25 of this capture are a nonzero, unparsed tail (b0200000000000000080ac44)
        // that is identical across all captured type-25 records; parse() does not read it.
        ReminderActivatedHistoryLog parsedRes = new ReminderActivatedHistoryLog();
        parsedRes.parse(Hex.decodeHex("1910f6f49d22317e070002000000b0200000000000000080ac44"));

        assertEquals(expected.verboseToString(), parsedRes.verboseToString());
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
        ReminderActivatedHistoryLog parsedRes = new ReminderActivatedHistoryLog();
        parsedRes.parse(Hex.decodeHex("1910f0228e226bd5060002000000b0200000000000000080ac44"));

        assertEquals(expected.verboseToString(), parsedRes.verboseToString());
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
