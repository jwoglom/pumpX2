package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

// Records from the maintainer's Tandem Mobi (Trio / TandemKit, Sept 2026), plus two already
// quoted in https://github.com/jwoglom/pumpx2/issues/148.
public class ReservoirEstimateHistoryLogTest {
    private static ReservoirEstimateHistoryLog build(long pumpTimeSec, long sequenceNum, int deliveredBits, int remainingBits, int displayed, long driveCounter) {
        return (ReservoirEstimateHistoryLog) new ReservoirEstimateHistoryLog(
                pumpTimeSec, sequenceNum, Float.intBitsToFloat(deliveredBits), Float.intBitsToFloat(remainingBits), displayed, driveCounter
        ).withHeaderHighNibble(1);
    }

    private static void assertFields(ReservoirEstimateHistoryLog parsed, int deliveredBits, int remainingBits, int displayed, long driveCounter) {
        assertEquals(deliveredBits, Float.floatToIntBits(parsed.getInsulinDelivered()));
        assertEquals(remainingBits, Float.floatToIntBits(parsed.getInsulinRemaining()));
        assertEquals(displayed, parsed.getDisplayedInsulinRemaining());
        assertEquals(driveCounter, parsed.getDriveCounter());
        assertEquals(ReservoirEstimateHistoryLog.OBSERVED_START_ESTIMATE, parsed.getInsulinDelivered() + parsed.getInsulinRemaining(), 0.001f);
    }

    // Written in the same second as CartridgeInserted: 0 U delivered, 203.94 U estimated, display 0.
    @Test
    public void testReservoirEstimateAtCartridgeInsertion() throws DecoderException {
        ReservoirEstimateHistoryLog expected = build(590440418L, 689017L, 0x00000000, 0x434beff2, 0, 524274L);

        ReservoirEstimateHistoryLog parsed = (ReservoirEstimateHistoryLog) HistoryLogMessageTester.testSingle(
                "e710e267312379830a0000000000f2ef4b4300000000f2ff0700",
                expected
        );
        assertEquals(590440418L, parsed.getPumpTimeSec());
        assertEquals(689017L, parsed.getSequenceNum());
        assertEquals(0.0f, parsed.getInsulinDelivered(), 0.0f);
        assertEquals(203.94f, parsed.getInsulinRemaining(), 0.01f);
        assertFields(parsed, 0x00000000, 0x434beff2, 0, 524274L);
        assertHexEquals(expected.getCargo(), parsed.getCargo());
    }

    // After the tubing fill: 20.00 U delivered (priming included), 183.94 U remaining, displayed 185.
    @Test
    public void testReservoirEstimateAfterFillRoundsToFive() throws DecoderException {
        ReservoirEstimateHistoryLog expected = build(590440871L, 689129L, 0x41a0030d, 0x4337ef90, 185, 545743L);

        ReservoirEstimateHistoryLog parsed = (ReservoirEstimateHistoryLog) HistoryLogMessageTester.testSingle(
                "e710a7693123e9830a000d03a04190ef3743b9000000cf530800",
                expected
        );
        assertEquals(20.0f, parsed.getInsulinDelivered(), 0.01f);
        assertEquals(183.94f, parsed.getInsulinRemaining(), 0.01f);
        assertFields(parsed, 0x41a0030d, 0x4337ef90, 185, 545743L);
        assertHexEquals(expected.getCargo(), parsed.getCargo());
    }

    // Below 40 U the displayed value is rounded down to a whole unit: 39.91 U reads 39.
    @Test
    public void testReservoirEstimateBelowFortyUnitsRoundsDown() throws DecoderException {
        ReservoirEstimateHistoryLog expected = build(590169753L, 673692L, 0x432405e3, 0x421fa83b, 39, 700351L);

        ReservoirEstimateHistoryLog parsed = (ReservoirEstimateHistoryLog) HistoryLogMessageTester.testSingle(
                "e71099462d239c470a00e30524433ba81f4227000000bfaf0a00",
                expected
        );
        assertEquals(164.02f, parsed.getInsulinDelivered(), 0.01f);
        assertEquals(39.91f, parsed.getInsulinRemaining(), 0.01f);
        assertFields(parsed, 0x432405e3, 0x421fa83b, 39, 700351L);
        assertHexEquals(expected.getCargo(), parsed.getCargo());
    }

    // 151.91 U remaining displays as 150 (nearest 5 U at or above 40 U).
    @Test
    public void testReservoirEstimateIssueExample() throws DecoderException {
        ReservoirEstimateHistoryLog expected = build(589541245L, 641361L, 0x42501f6d, 0x4317e816, 150, 580144L);

        ReservoirEstimateHistoryLog parsed = (ReservoirEstimateHistoryLog) HistoryLogMessageTester.testSingle(
                "e7107daf232351c909006d1f504216e817439600000030da0800",
                expected
        );
        assertFields(parsed, 0x42501f6d, 0x4317e816, 150, 580144L);
        assertHexEquals(expected.getCargo(), parsed.getCargo());
    }

    // During a tubing fill the displayed value reads 0.
    @Test
    public void testReservoirEstimateDuringFill() throws DecoderException {
        ReservoirEstimateHistoryLog expected = build(589584836L, 643636L, 0x42b87193, 0x42df6e50, 0, 623290L);

        ReservoirEstimateHistoryLog parsed = (ReservoirEstimateHistoryLog) HistoryLogMessageTester.testSingle(
                "e710c459242334d209009371b842506edf4200000000ba820900",
                expected
        );
        assertFields(parsed, 0x42b87193, 0x42df6e50, 0, 623290L);
        assertHexEquals(expected.getCargo(), parsed.getCargo());
    }
}
