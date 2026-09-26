package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BleSoftDeviceVersionHistoryLogTest {

    private static void assertVersion720(BleSoftDeviceVersionHistoryLog log) {
        assertTrue(log.isKnown());
        assertEquals(132, log.getSoftDeviceId());
        assertEquals(7, log.getSoftDeviceMajorVersion());
        assertEquals(2, log.getSoftDeviceMinorVersion());
        assertEquals(0, log.getSoftDeviceBugfixVersion());
        assertEquals(132, log.getSoftDeviceId2());
        assertEquals(7, log.getSoftDeviceMajorVersion2());
        assertEquals(2, log.getSoftDeviceMinorVersion2());
        assertEquals(0, log.getSoftDeviceBugfixVersion2());
        assertEquals("7.2.0", log.getVersionString());
        assertEquals("7.2.0", log.getVersionString2());
    }

    @Test
    public void testBleSoftDeviceVersion_x2MidnightBlock() throws DecoderException {
        // Maintainer's t:slim X2 (2024). Header high nibble 0. Written after NewDay, right after the last 449.
        BleSoftDeviceVersionHistoryLog expected = new BleSoftDeviceVersionHistoryLog(
                // long pumpTimeSec, long sequenceNum, int softDeviceId, int softDeviceMajorVersion, int softDeviceMinorVersion, int softDeviceBugfixVersion, int softDeviceId2, int softDeviceMajorVersion2, int softDeviceMinorVersion2, int softDeviceBugfixVersion2
                534211223L, 1051210L, 132, 7, 2, 0, 132, 7, 2, 0
        );

        BleSoftDeviceVersionHistoryLog parsedRes = (BleSoftDeviceVersionHistoryLog) HistoryLogMessageTester.testSingle(
                "c201976ad71f4a0a100084000700020000008400070002000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertVersion720(parsedRes);
    }

    @Test
    public void testBleSoftDeviceVersion_x2FirstBootAfterFirmwareUpdate() throws DecoderException {
        // Maintainer's t:slim X2 (2023). Header high nibble 0. Written 8 s after the ArmInit of the
        // first boot on updated firmware; the previous firmware wrote no 450 records.
        BleSoftDeviceVersionHistoryLog expected = new BleSoftDeviceVersionHistoryLog(
                503757910L, 2107772L, 132, 7, 2, 0, 132, 7, 2, 0
        );

        BleSoftDeviceVersionHistoryLog parsedRes = (BleSoftDeviceVersionHistoryLog) HistoryLogMessageTester.testSingle(
                "c20156bc061e7c29200084000700020000008400070002000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertVersion720(parsedRes);
    }

    @Test
    public void testBleSoftDeviceVersion_mobiMidnightBlock() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2). Header high nibble 1. Same values as the X2.
        BleSoftDeviceVersionHistoryLog expected = (BleSoftDeviceVersionHistoryLog) new BleSoftDeviceVersionHistoryLog(
                590112048L, 670876L, 132, 7, 2, 0, 132, 7, 2, 0
        ).withHeaderHighNibble(1);

        BleSoftDeviceVersionHistoryLog parsedRes = (BleSoftDeviceVersionHistoryLog) HistoryLogMessageTester.testSingle(
                "c21130652c239c3c0a0084000700020000008400070002000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertVersion720(parsedRes);
    }

    @Test
    public void testBleSoftDeviceVersion_mobiBootAfterFirmwareUpdate() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.2). Header high nibble 1. First boot after the update
        // from 7.9.0.1; the value did not change.
        BleSoftDeviceVersionHistoryLog expected = (BleSoftDeviceVersionHistoryLog) new BleSoftDeviceVersionHistoryLog(
                564414486L, 423427L, 132, 7, 2, 0, 132, 7, 2, 0
        ).withHeaderHighNibble(1);

        BleSoftDeviceVersionHistoryLog parsedRes = (BleSoftDeviceVersionHistoryLog) HistoryLogMessageTester.testSingle(
                "c2111648a4210376060084000700020000008400070002000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertVersion720(parsedRes);
    }

    @Test
    public void testBleSoftDeviceVersion_mobiAllZeroDuringBoot() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.1), clock not yet set. Header high nibble 1. NewDay block
        // written 7 s after ArmInit on a restart on a new day, before the BLE processor reported its
        // versions (the 449 records before it have zero version words).
        BleSoftDeviceVersionHistoryLog expected = (BleSoftDeviceVersionHistoryLog) new BleSoftDeviceVersionHistoryLog(
                379657371L, 46L, 0, 0, 0, 0, 0, 0, 0, 0
        ).withHeaderHighNibble(1);

        BleSoftDeviceVersionHistoryLog parsedRes = (BleSoftDeviceVersionHistoryLog) HistoryLogMessageTester.testSingle(
                "c2119b1ca1162e00000000000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertFalse(parsedRes.isKnown());
        assertEquals(0, parsedRes.getSoftDeviceId());
        assertEquals(0, parsedRes.getSoftDeviceId2());
        assertNull(parsedRes.getVersionString());
        assertNull(parsedRes.getVersionString2());
    }

    @Test
    public void testBleSoftDeviceVersion_mobiFilledInThreeSecondsLater() throws DecoderException {
        // Maintainer's Tandem Mobi (7.9.0.1), same boot as the all-zero record: 3 s and 20 records
        // later the boot block writes the real values.
        BleSoftDeviceVersionHistoryLog expected = (BleSoftDeviceVersionHistoryLog) new BleSoftDeviceVersionHistoryLog(
                379657374L, 66L, 132, 7, 2, 0, 132, 7, 2, 0
        ).withHeaderHighNibble(1);

        BleSoftDeviceVersionHistoryLog parsedRes = (BleSoftDeviceVersionHistoryLog) HistoryLogMessageTester.testSingle(
                "c2119e1ca1164200000084000700020000008400070002000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertVersion720(parsedRes);
        assertEquals(3L, parsedRes.getPumpTimeSec() - 379657371L);
    }
}
