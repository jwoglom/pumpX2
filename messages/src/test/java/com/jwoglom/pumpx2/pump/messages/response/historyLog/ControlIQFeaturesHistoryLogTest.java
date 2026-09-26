package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesV2Response.ControlIqFeatureType;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.Set;

public class ControlIQFeaturesHistoryLogTest {

    @Test
    public void testControlIQFeatures_x2Api25_unsupportedIsZero() throws DecoderException {
        // Maintainer's t:slim X2 (API 2.5), daily block. Header high nibble 0.
        // This firmware answers PumpFeaturesV2 CONTROL_IQ_FEATURES with status 1.
        ControlIQFeaturesHistoryLog expected = new ControlIQFeaturesHistoryLog(
                // long pumpTimeSec, long sequenceNum, long controlIqFeaturesBitmask
                461462412L, 23511L, 0L
        );

        ControlIQFeaturesHistoryLog parsedRes = (ControlIQFeaturesHistoryLog) HistoryLogMessageTester.testSingle(
                "48018c5b811bd75b000000000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0L, parsedRes.getControlIqFeaturesBitmask());
        assertTrue(parsedRes.getControlIqFeatures().isEmpty());
        assertArrayEquals(new byte[12], parsedRes.getUnknownTail());
    }

    @Test
    public void testControlIQFeatures_x2FirstBootAfterFirmwareUpdate() throws DecoderException {
        // Maintainer's t:slim X2, first boot on API 3.2 firmware (after ArmInit, VersionInfo and
        // opcode 312). Header high nibble 0. Daily records earlier the same day read 0.
        ControlIQFeaturesHistoryLog expected = new ControlIQFeaturesHistoryLog(
                503757902L, 2107760L, 49L
        );

        ControlIQFeaturesHistoryLog parsedRes = (ControlIQFeaturesHistoryLog) HistoryLogMessageTester.testSingle(
                "48014ebc061e7029200031000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(49L, parsedRes.getControlIqFeaturesBitmask());
    }

    @Test
    public void testControlIQFeatures_x2Api34_daily() throws DecoderException {
        // Maintainer's t:slim X2 (API 3.4), daily block. Header high nibble 0.
        // PumpFeaturesV2 CONTROL_IQ_FEATURES on this pump returned 49 as well.
        ControlIQFeaturesHistoryLog expected = new ControlIQFeaturesHistoryLog(
                534211223L, 1051211L, 49L
        );

        ControlIQFeaturesHistoryLog parsedRes = (ControlIQFeaturesHistoryLog) HistoryLogMessageTester.testSingle(
                "4801976ad71f4b0a100031000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(49L, parsedRes.getControlIqFeaturesBitmask());
        assertEquals(Set.of(ControlIqFeatureType.TIMED_EXERCISE), parsedRes.getControlIqFeatures());
        // bits 4 and 5 are set but have no name
        assertEquals(0x30L, parsedRes.getControlIqFeaturesBitmask() & 0x30L);
    }

    @Test
    public void testControlIQFeatures_mobi7701_boot() throws DecoderException {
        // Maintainer's Tandem Mobi (7.7.0.1), boot after a restart. Header high nibble 1.
        // PumpFeaturesV2 CONTROL_IQ_FEATURES read 3072 in the same session.
        ControlIQFeaturesHistoryLog expected = (ControlIQFeaturesHistoryLog) new ControlIQFeaturesHistoryLog(
                539917225L, 122684L, 3072L
        ).withHeaderHighNibble(1);

        ControlIQFeaturesHistoryLog parsedRes = (ControlIQFeaturesHistoryLog) HistoryLogMessageTester.testSingle(
                "4811a97b2e203cdf0100000c0000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(3072L, parsedRes.getControlIqFeaturesBitmask());
        assertEquals(Set.of(ControlIqFeatureType.EXTENDED_BOLUS_ENABLED), parsedRes.getControlIqFeatures());
        // bit 10 is set but has no name
        assertEquals(0x400L, parsedRes.getControlIqFeaturesBitmask() & 0x400L);
    }

    @Test
    public void testControlIQFeatures_mobiFirstBootOn7901() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.1), first boot after updating from 7.7.0.1. Header high
        // nibble 1. The same pump's earlier records read 3072.
        ControlIQFeaturesHistoryLog expected = (ControlIQFeaturesHistoryLog) new ControlIQFeaturesHistoryLog(
                552609115L, 614874L, 896L
        ).withHeaderHighNibble(1);

        ControlIQFeaturesHistoryLog parsedRes = (ControlIQFeaturesHistoryLog) HistoryLogMessageTester.testSingle(
                "48115b25f020da61090080030000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(896L, parsedRes.getControlIqFeaturesBitmask());
    }

    @Test
    public void testControlIQFeatures_mobi7902_daily() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2), daily block. Header high nibble 1.
        ControlIQFeaturesHistoryLog expected = (ControlIQFeaturesHistoryLog) new ControlIQFeaturesHistoryLog(
                590112048L, 670877L, 896L
        ).withHeaderHighNibble(1);

        ControlIQFeaturesHistoryLog parsedRes = (ControlIQFeaturesHistoryLog) HistoryLogMessageTester.testSingle(
                "481130652c239d3c0a0080030000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(896L, parsedRes.getControlIqFeaturesBitmask());
        assertEquals(
                Set.of(ControlIqFeatureType.CONTROL_IQ_VERSION_ONE_FIVE, ControlIqFeatureType.TEMP_RATE_ENABLED),
                parsedRes.getControlIqFeatures());
        // bit 7 is set but has no name
        assertEquals(0x80L, parsedRes.getControlIqFeaturesBitmask() & 0x80L);
        assertArrayEquals(new byte[12], parsedRes.getUnknownTail());
    }
}
