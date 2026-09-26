package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpDriveEventHistoryLogTest {
    // Mobi, the start and end records of a 1.5 U bolus: 666882 - 665316 = 1566 = 1.45 U x 1080.
    @Test
    public void testMobiBolusStartAndEnd() throws DecoderException {
        PumpDriveEventHistoryLog start = new PumpDriveEventHistoryLog(
                // long pumpTimeSec, long sequenceNum, long driveCounter, int unknownI16At14, int unknownI16At16, int unknownU8At18, int unknownU8At19, int unknownU8At20, int unknownU8At21, int unknownU16At22, int unknownU16At24, int headerHighNibble
                590115449L, 670998L, 665316L, 45, 45, 0x64, 4, 2, 15, 0xFFFF, 7, 1
        );
        PumpDriveEventHistoryLog parsedStart = (PumpDriveEventHistoryLog) HistoryLogMessageTester.testSingle(
                "151179722c23163d0a00e4260a002d002d006404020fffff0700",
                start
        );
        assertHexEquals(start.getCargo(), parsedStart.getCargo());

        PumpDriveEventHistoryLog end = new PumpDriveEventHistoryLog(
                590115492L, 671010L, 666882L, 54, 54, 0x64, 4, 2, 15, 0xFFFF, 12, 1
        );
        PumpDriveEventHistoryLog parsedEnd = (PumpDriveEventHistoryLog) HistoryLogMessageTester.testSingle(
                "1511a4722c23223d0a00022d0a00360036006404020fffff0c00",
                end
        );
        assertHexEquals(end.getCargo(), parsedEnd.getCargo());
        assertEquals(1566L, parsedEnd.getDriveCounter() - parsedStart.getDriveCounter());
    }

    // Mobi, cartridge retraction: the int16 fields are negative.
    @Test
    public void testMobiRetraction() throws DecoderException {
        PumpDriveEventHistoryLog expected = new PumpDriveEventHistoryLog(
                590440357L, 689006L, 556233L, -1999, -2000, 0xC4, 4, 2, 15, 441, 73, 1
        );

        PumpDriveEventHistoryLog parsedRes = (PumpDriveEventHistoryLog) HistoryLogMessageTester.testSingle(
                "1511a56731236e830a00c97c080031f830f8c404020fb9014900",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(-1999, parsedRes.getUnknownI16At14());
        assertEquals(-2000, parsedRes.getUnknownI16At16());
    }

    // Mobi, tubing fill after a cartridge change.
    @Test
    public void testMobiFill() throws DecoderException {
        PumpDriveEventHistoryLog expected = new PumpDriveEventHistoryLog(
                590440428L, 689022L, 524873L, 298, 300, 0x64, 4, 2, 15, 0xFFFF, 14, 1
        );

        PumpDriveEventHistoryLog parsedRes = (PumpDriveEventHistoryLog) HistoryLogMessageTester.testSingle(
                "1511ec6731237e830a00490208002a012c016404020fffff0e00",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(524873L, parsedRes.getDriveCounter());
    }
}
