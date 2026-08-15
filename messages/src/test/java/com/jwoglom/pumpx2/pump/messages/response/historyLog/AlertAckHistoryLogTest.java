package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlertAckHistoryLogTest {
    // AlertAckHistoryLog (typeId 27) is not registered in
    // HistoryLogParser.LOG_MESSAGE_TYPES, so HistoryLogParser.parse() currently returns an
    // UnknownHistoryLog for real captures of this type instead of dispatching here. That is a
    // pre-existing gap outside this batch's scope (no production code is modified by these
    // tests), so these tests call AlertAckHistoryLog#parse directly to verify its own decoding
    // logic against a real capture.
    @Test
    public void testAlertAckHistoryLog1() throws DecoderException {
        AlertAckHistoryLog expected = new AlertAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580260938L, 469779L, 50
        );

        AlertAckHistoryLog parsedRes = new AlertAckHistoryLog();
        parsedRes.parse(Hex.decodeHex("1b104a149622132b070032000000000000000000000000000000"));

        assertEquals(expected.verboseToString(), parsedRes.verboseToString());
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }

    @Test
    public void testAlertAckHistoryLog2() throws DecoderException {
        AlertAckHistoryLog expected = new AlertAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            580095340L, 462908L, 0
        );

        AlertAckHistoryLog parsedRes = new AlertAckHistoryLog();
        parsedRes.parse(Hex.decodeHex("1b106c8d93223c10070000000000000000000000000000000000"));

        assertEquals(expected.verboseToString(), parsedRes.verboseToString());
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
