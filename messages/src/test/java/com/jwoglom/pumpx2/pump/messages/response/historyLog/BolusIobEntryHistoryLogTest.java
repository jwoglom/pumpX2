package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusIobEntryHistoryLogTest {
    // Mobi driven by Trio, 0.2 U BLE bolus 2904, 5 h insulin duration.
    @Test
    public void testMobiBolus() throws DecoderException {
        BolusIobEntryHistoryLog expected = new BolusIobEntryHistoryLog(
                // long pumpTimeSec, long sequenceNum, int unknownU8At10, int unknownU8At11, long insulinDurationMillis, float insulinDelivered, float mudaliarIob, int headerHighNibble
                589529251L, 640857L, 0, 4, 18000000L,
                Float.intBitsToFloat(0x3e4ccccd), // 0.2
                Float.intBitsToFloat(0x3f731a58), // ~0.9496
                1
        );

        BolusIobEntryHistoryLog parsedRes = (BolusIobEntryHistoryLog) HistoryLogMessageTester.testSingle(
                "c310a380232359c709000004000080a81201cdcc4c3e581a733f",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(4, parsedRes.getUnknownU8At11());
        assertEquals(18000000L, parsedRes.getInsulinDurationMillis());
        assertEquals(0x3e4ccccd, Float.floatToIntBits(parsedRes.getInsulinDelivered()));
    }

    // t:slim X2 on a profile with an 8 h insulin duration.
    @Test
    public void testX2EightHourInsulinDuration() throws DecoderException {
        BolusIobEntryHistoryLog expected = new BolusIobEntryHistoryLog(
                461510714L, 24321L, 0, 0, 28800000L, 1.0F, 1.0F
        );

        BolusIobEntryHistoryLog parsedRes = (BolusIobEntryHistoryLog) HistoryLogMessageTester.testSingle(
                "c3003a18821b015f0000000000000074b7010000803f0000803f",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(28800000L, parsedRes.getInsulinDurationMillis());
    }

    // t:slim X2, a bolus that delivered nothing.
    @Test
    public void testX2NothingDelivered() throws DecoderException {
        BolusIobEntryHistoryLog expected = new BolusIobEntryHistoryLog(
                461638673L, 26934L, 1, 0xFF, 0xFFFFFFFFL, 0.0F, 0.0F
        );

        BolusIobEntryHistoryLog parsedRes = (BolusIobEntryHistoryLog) HistoryLogMessageTester.testSingle(
                "c300110c841b3669000001ff0000ffffffff0000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(1, parsedRes.getUnknownU8At10());
        assertEquals(0xFF, parsedRes.getUnknownU8At11());
        assertEquals(0xFFFFFFFFL, parsedRes.getInsulinDurationMillis());
    }
}
