package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

// Records from the maintainer's pumps: a Tandem Mobi (Sept 2026; a triple press that snoozed an
// alert, and the press that started a tubing prime) and a t:slim X2 (Aug 2022, official app).
public class PumpButtonHistoryLogTest {
    private static PumpButtonHistoryLog check(String hex, int nibble, long pumpTimeSec, long sequenceNum, long timeOfDayMs, int state) throws DecoderException {
        PumpButtonHistoryLog expected = (PumpButtonHistoryLog) new PumpButtonHistoryLog(
                pumpTimeSec, sequenceNum, timeOfDayMs, state
        ).withHeaderHighNibble(nibble);
        PumpButtonHistoryLog parsed = (PumpButtonHistoryLog) HistoryLogMessageTester.testSingle(hex, expected);
        assertEquals(pumpTimeSec, parsed.getPumpTimeSec());
        assertEquals(sequenceNum, parsed.getSequenceNum());
        assertEquals(timeOfDayMs, parsed.getTimeOfDayMs());
        assertEquals(state, parsed.getState());
        assertEquals(pumpTimeSec % 86400, parsed.getTimeOfDayMs() / 1000);
        assertHexEquals(expected.getCargo(), parsed.getCargo());
        return parsed;
    }

    @Test
    public void testMobiPressThenRelease() throws DecoderException {
        PumpButtonHistoryLog press = check("11102c713823d0e20a002a50b600010000000000000000000000", 1, 590901548L, 713424L, 11948074L, 1);
        PumpButtonHistoryLog release = check("11102c713823d1e20a00b750b600000000000000000000000000", 1, 590901548L, 713425L, 11948215L, 0);
        assertTrue(press.isPressed());
        assertFalse(release.isPressed());
        assertEquals(141L, release.getTimeOfDayMs() - press.getTimeOfDayMs());
    }

    @Test
    public void testMobiPressStartingTubingPrime() throws DecoderException {
        PumpButtonHistoryLog press = check("1110ec6731237c830a00f2562004010000000000000000000000", 1, 590440428L, 689020L, 69228274L, 1);
        assertTrue(press.isPressed());
    }

    @Test
    public void testX2PressAndRelease() throws DecoderException {
        PumpButtonHistoryLog press = check("11009d4a831b15640000c2ae6702010000000000000000000000", 0, 461589149L, 25621L, 40349378L, 1);
        PumpButtonHistoryLog release = check("11009d4a831b1664000044af6702000000000000000000000000", 0, 461589149L, 25622L, 40349508L, 0);
        assertTrue(press.isPressed());
        assertFalse(release.isPressed());
        assertEquals(130L, release.getTimeOfDayMs() - press.getTimeOfDayMs());
    }
}
