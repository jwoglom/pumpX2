package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import com.jwoglom.pumpx2.shared.Hex;

public class SoftwareIdHistoryLogTest {

    // Maintainer's Tandem Mobi (7.9.0.2). Header high nibble 1.
    // The four records of one daily block, in the order the pump wrote them.
    @Test
    public void testSoftwareId_mobiDailySlots() throws DecoderException {
        String[] hex = {
                "c11130652c23983c0a0000000000d51ac9d328b2124c00000000",
                "c11130652c23993c0a0002000000d51ac9d328b2124c00000000",
                "c11130652c239a3c0a0001000000d51ac9d328b2124c00000000",
                "c11130652c239b3c0a0003000000d51ac9d328b2124c00000000",
        };
        int[] slots = {0, 2, 1, 3};
        for (int i = 0; i < hex.length; i++) {
            SoftwareIdHistoryLog expected = (SoftwareIdHistoryLog) new SoftwareIdHistoryLog(
                    // long pumpTimeSec, long sequenceNum, int slot, int unknown11, long armSwVersion, long softwareIdLow, long unknown22
                    590112048L, 670872L + i, slots[i], 0, 3553172181L, 1276293672L, 0L
            ).withHeaderHighNibble(1);

            SoftwareIdHistoryLog parsedRes = (SoftwareIdHistoryLog) HistoryLogMessageTester.testSingle(
                    hex[i],
                    expected
            );
            assertHexEquals(expected.getCargo(), parsedRes.getCargo());
            assertEquals(slots[i], parsedRes.getSlot());
            assertEquals(3553172181L, parsedRes.getArmSwVersion());
            assertEquals(1276293672L, parsedRes.getSoftwareIdLow());
            assertTrue(parsedRes.isSoftwareIdKnown());
            assertEquals("d3c91ad54c12b228", parsedRes.getSoftwareId());
            assertEquals(0, parsedRes.getUnknown11());
            assertEquals(0L, parsedRes.getUnknown22());
        }
    }

    // Maintainer's Tandem Mobi (7.9.0.2). Header high nibble 1.
    // The VersionsA record written immediately before the slots above carries the same high word.
    @Test
    public void testSoftwareId_highWordIsVersionsAArmSwVersion() throws DecoderException {
        VersionsAHistoryLog versions = (VersionsAHistoryLog) HistoryLogParser.parse(
                Hex.decodeHex("331130652c23973c0a00b3870f00d51ac9d3b3870f00d51ac9d3"));
        SoftwareIdHistoryLog softwareId = (SoftwareIdHistoryLog) HistoryLogParser.parse(
                Hex.decodeHex("c11130652c23983c0a0000000000d51ac9d328b2124c00000000"));

        assertEquals(670871L, versions.getSequenceNum());
        assertEquals(670872L, softwareId.getSequenceNum());
        assertEquals(versions.getArmSwVersion(), softwareId.getArmSwVersion());
    }

    // Maintainer's t:slim X2 (2024). Header high nibble 0.
    // The X2 writes only slots 0 and 2.
    @Test
    public void testSoftwareId_x2DailySlots() throws DecoderException {
        String[] hex = {
                "c101976ad71f480a1000000000000c1f365cbecffebb00000000",
                "c101976ad71f490a1000020000000c1f365cbecffebb00000000",
        };
        int[] slots = {0, 2};
        for (int i = 0; i < hex.length; i++) {
            SoftwareIdHistoryLog expected = new SoftwareIdHistoryLog(
                    534211223L, 1051208L + i, slots[i], 1547050764L, 3154038718L
            );

            SoftwareIdHistoryLog parsedRes = (SoftwareIdHistoryLog) HistoryLogMessageTester.testSingle(
                    hex[i],
                    expected
            );
            assertHexEquals(expected.getCargo(), parsedRes.getCargo());
            assertEquals(slots[i], parsedRes.getSlot());
            assertEquals("5c361f0cbbfecfbe", parsedRes.getSoftwareId());
        }
    }

    // Maintainer's Tandem Mobi (7.9.0.1). Header high nibble 1.
    // Boot across midnight: the NewDay block logs slot 0 before it is filled, and the filled slot 0
    // follows a second later.
    @Test
    public void testSoftwareId_unfilledBootSlot() throws DecoderException {
        SoftwareIdHistoryLog expectedUnfilled = (SoftwareIdHistoryLog) new SoftwareIdHistoryLog(
                379657371L, 42L, 0, 0L, 0L
        ).withHeaderHighNibble(1);

        SoftwareIdHistoryLog unfilled = (SoftwareIdHistoryLog) HistoryLogMessageTester.testSingle(
                "c1119b1ca1162a00000000000000000000000000000000000000",
                expectedUnfilled
        );
        assertHexEquals(expectedUnfilled.getCargo(), unfilled.getCargo());
        assertEquals(0, unfilled.getSlot());
        assertFalse(unfilled.isSoftwareIdKnown());
        assertEquals("", unfilled.getSoftwareId());

        SoftwareIdHistoryLog expectedFilled = (SoftwareIdHistoryLog) new SoftwareIdHistoryLog(
                379657372L, 48L, 0, 1108201743L, 390327166L
        ).withHeaderHighNibble(1);

        SoftwareIdHistoryLog filled = (SoftwareIdHistoryLog) HistoryLogMessageTester.testSingle(
                "c1119c1ca11630000000000000000fd10d427eeb431700000000",
                expectedFilled
        );
        assertHexEquals(expectedFilled.getCargo(), filled.getCargo());
        assertTrue(filled.isSoftwareIdKnown());
        assertEquals("420dd10f1743eb7e", filled.getSoftwareId());
    }

    // Maintainer's t:slim X2 (2024). Header high nibble 0.
    // Unfilled slot 0 logged in the NewDay block of a boot.
    @Test
    public void testSoftwareId_x2UnfilledBootSlot() throws DecoderException {
        SoftwareIdHistoryLog expected = new SoftwareIdHistoryLog(
                505928865L, 43L, 0, 0L, 0L
        );

        SoftwareIdHistoryLog parsedRes = (SoftwareIdHistoryLog) HistoryLogMessageTester.testSingle(
                "c101a1dc271e2b00000000000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertFalse(parsedRes.isSoftwareIdKnown());
        assertEquals("", parsedRes.getSoftwareId());
    }
}
