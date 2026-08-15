package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlarmAckHistoryLogTest {
    // AlarmAckHistoryLog (typeId 8) is not registered in
    // HistoryLogParser.LOG_MESSAGE_TYPES, so HistoryLogParser.parse() currently returns an
    // UnknownHistoryLog for real captures of this type instead of dispatching here. That is a
    // pre-existing gap outside this batch's scope (no production code is modified by these
    // tests), so these tests call AlarmAckHistoryLog#parse directly to verify its own decoding
    // logic against a real capture.
    @Test
    public void testAlarmAckHistoryLog1() throws DecoderException {
        AlarmAckHistoryLog expected = new AlarmAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alarmId
            580777655L, 491076L, 18
        );

        AlarmAckHistoryLog parsedRes = new AlarmAckHistoryLog();
        parsedRes.parse(Hex.decodeHex("0810b7f69d22447e070012000000000000000000000000000000"));

        assertEquals(expected.verboseToString(), parsedRes.verboseToString());
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }

    @Test
    public void testAlarmAckHistoryLog2() throws DecoderException {
        AlarmAckHistoryLog expected = new AlarmAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alarmId
            579744825L, 448164L, 18
        );

        AlarmAckHistoryLog parsedRes = new AlarmAckHistoryLog();
        parsedRes.parse(Hex.decodeHex("081039348e22a4d6060012000000000000000000000000000000"));

        assertEquals(expected.verboseToString(), parsedRes.verboseToString());
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
