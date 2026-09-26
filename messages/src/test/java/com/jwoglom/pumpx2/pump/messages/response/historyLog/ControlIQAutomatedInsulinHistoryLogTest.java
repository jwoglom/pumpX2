package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlIQAutomatedInsulinHistoryLogTest {

    private static ControlIQAutomatedInsulinHistoryLog parse(String hex, ControlIQAutomatedInsulinHistoryLog expected) throws DecoderException {
        ControlIQAutomatedInsulinHistoryLog parsedRes = (ControlIQAutomatedInsulinHistoryLog) HistoryLogMessageTester.testSingle(hex, expected);
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        return parsedRes;
    }

    @Test
    public void testAutoBolusInWindow_tslimX2() throws DecoderException {
        // Maintainer's t:slim X2 (2024). A 0.823 U Control-IQ automatic bolus started 9 minutes
        // earlier; the previous 237 had total 1.3483579 and no auto-bolus.
        ControlIQAutomatedInsulinHistoryLog expected = new ControlIQAutomatedInsulinHistoryLog(
                // long pumpTimeSec, long sequenceNum, float totalInsulin, float basalAboveProfileInsulin, float autoBolusInsulin, long unknown22
                534380723L, 1059847L, 2.1718454F, 5.6028366e-06F, 0.82348716F, 0L
        );

        ControlIQAutomatedInsulinHistoryLog parsedRes = parse(
                "ed00b300da1f072c100084ff0a400000bc360ed0523f00000000",
                expected
        );
        assertEquals(0.823, parsedRes.getAutoBolusInsulin(), 0.0005);
        assertEquals(1.3483579, parsedRes.getBasalInsulin(), 0.00001);
        assertTrue(Math.abs(parsedRes.getBasalAboveProfileInsulin()) < ControlIQAutomatedInsulinHistoryLog.RESIDUE_THRESHOLD_UNITS);
        assertTrue(parsedRes.hasAutomatedInsulin());
        assertEquals(0L, parsedRes.getUnknown22());
    }

    @Test
    public void testAutoBolusLeavesWindowAfterTwoHours_tslimX2() throws DecoderException {
        // Maintainer's t:slim X2 (2024), same pump and auto-bolus as testAutoBolusInWindow_tslimX2.
        ControlIQAutomatedInsulinHistoryLog before = parse(
                "ed002ffdd91fae2b1000fe96ac3f0000bc36000080b400000000",
                new ControlIQAutomatedInsulinHistoryLog(534379823L, 1059758L, 1.3483579F, 5.6028366e-06F, -2.3841858e-07F, 0L)
        );
        ControlIQAutomatedInsulinHistoryLog booked = parse(
                "ed00b300da1f072c100084ff0a400000bc360ed0523f00000000",
                new ControlIQAutomatedInsulinHistoryLog(534380723L, 1059847L, 2.1718454F, 5.6028366e-06F, 0.82348716F, 0L)
        );
        ControlIQAutomatedInsulinHistoryLog stillIn = parse(
                "ed004f19da1f472d10004e41ee3f0000bc360ed0523f00000000",
                new ControlIQAutomatedInsulinHistoryLog(534387023L, 1060167L, 1.861368F, 5.6028366e-06F, 0.82348716F, 0L)
        );
        ControlIQAutomatedInsulinHistoryLog expired = parse(
                "ed00d31cda1f6d2d100017b7a73f07788b3e000080b400000000",
                new ControlIQAutomatedInsulinHistoryLog(534387923L, 1060205L, 1.310275F, 0.2724001F, -2.3841858e-07F, 0L)
        );

        assertTrue(Math.abs(before.getAutoBolusInsulin()) < ControlIQAutomatedInsulinHistoryLog.RESIDUE_THRESHOLD_UNITS);
        assertEquals(6300, stillIn.getPumpTimeSec() - booked.getPumpTimeSec());
        assertEquals(booked.getAutoBolusInsulin(), stillIn.getAutoBolusInsulin(), 0F);
        assertEquals(7200, expired.getPumpTimeSec() - booked.getPumpTimeSec());
        assertTrue(Math.abs(expired.getAutoBolusInsulin()) < ControlIQAutomatedInsulinHistoryLog.RESIDUE_THRESHOLD_UNITS);
        assertEquals(before.getAutoBolusInsulin(), expired.getAutoBolusInsulin(), 0F);
    }

    @Test
    public void testBasalAboveProfileAndAutoBolus_tslimX2_2022() throws DecoderException {
        // Maintainer's t:slim X2 (2022). A 1.555 U automatic bolus 8 minutes earlier; Control-IQ
        // also ran basal above the profile rate in the window.
        ControlIQAutomatedInsulinHistoryLog expected = new ControlIQAutomatedInsulinHistoryLog(
                446086341L, 184199L, 2.739373F, 0.21560338F, 1.5547314F, 0L
        );

        ControlIQAutomatedInsulinHistoryLog parsedRes = parse(
                "ed00c5bc961a87cf0200e3512f4022c75c3e7001c73f00000000",
                expected
        );
        assertEquals(1.555, parsedRes.getAutoBolusInsulin(), 0.0005);
        assertEquals(0.2156, parsedRes.getBasalAboveProfileInsulin(), 0.0001);
        assertEquals(0.9690, parsedRes.getTotalInsulin() - parsedRes.getBasalAboveProfileInsulin() - parsedRes.getAutoBolusInsulin(), 0.0001);
    }

    @Test
    public void testClosedLoopWithoutAutoBolus_tslimX2() throws DecoderException {
        // Maintainer's t:slim X2 (a second pump, 2024). No automatic bolus in the window.
        ControlIQAutomatedInsulinHistoryLog expected = new ControlIQAutomatedInsulinHistoryLog(
                509940593L, 2357891L, 2.8286963F, 1.2953609F, 0F, 0L
        );

        ControlIQAutomatedInsulinHistoryLog parsedRes = parse(
                "ed007113651e83fa23005c09354063cea53f0000000000000000",
                expected
        );
        assertEquals(0F, parsedRes.getAutoBolusInsulin(), 0F);
        assertEquals(parsedRes.getTotalInsulin(), parsedRes.getBasalInsulin(), 0F);
        assertTrue(parsedRes.getBasalAboveProfileInsulin() < parsedRes.getTotalInsulin());
        assertTrue(parsedRes.hasAutomatedInsulin());
    }

    @Test
    public void testClosedLoop_mobi() throws DecoderException {
        // Maintainer's Tandem Mobi (2025). Header high nibble 1. autoBolusInsulin is float residue.
        ControlIQAutomatedInsulinHistoryLog expected = (ControlIQAutomatedInsulinHistoryLog) new ControlIQAutomatedInsulinHistoryLog(
                539480273L, 106164L, 1.9754617F, 0.52888036F, 5.9604645e-07F, 0L
        ).withHeaderHighNibble(1);

        ControlIQAutomatedInsulinHistoryLog parsedRes = parse(
                "ed10d1d02720b49e0100eedbfc3fb464073f0000203500000000",
                expected
        );
        assertEquals(0.5289, parsedRes.getBasalAboveProfileInsulin(), 0.0001);
        assertTrue(Math.abs(parsedRes.getAutoBolusInsulin()) < ControlIQAutomatedInsulinHistoryLog.RESIDUE_THRESHOLD_UNITS);
        assertTrue(parsedRes.hasAutomatedInsulin());
    }

    @Test
    public void testControlIqOff_mobi() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2). Header high nibble 1. Control-IQ off, never in closed
        // loop since the pump started: exactly zero.
        ControlIQAutomatedInsulinHistoryLog zero = parse(
                "ed1088672c23bc3c0a0000000000000000000000000000000000",
                (ControlIQAutomatedInsulinHistoryLog) new ControlIQAutomatedInsulinHistoryLog(
                        590112648L, 670908L, 0F, 0F, 0F, 0L
                ).withHeaderHighNibble(1)
        );
        assertEquals(0F, zero.getTotalInsulin(), 0F);
        assertEquals(0F, zero.getBasalAboveProfileInsulin(), 0F);
        assertEquals(0F, zero.getAutoBolusInsulin(), 0F);
        assertFalse(zero.hasAutomatedInsulin());

        // Maintainer's Tandem Mobi (2026). Header high nibble 1. Control-IQ off for days after
        // earlier closed-loop use: the sums hold float residue instead of zero.
        ControlIQAutomatedInsulinHistoryLog residue = parse(
                "ed1040e6de220c8209000000b4b5000040340000000000000000",
                (ControlIQAutomatedInsulinHistoryLog) new ControlIQAutomatedInsulinHistoryLog(
                        585033280L, 623116L, -1.3411045e-06F, 1.7881393e-07F, 0F, 0L
                ).withHeaderHighNibble(1)
        );
        assertTrue(residue.getTotalInsulin() != 0F);
        assertFalse(residue.hasAutomatedInsulin());
    }
}
