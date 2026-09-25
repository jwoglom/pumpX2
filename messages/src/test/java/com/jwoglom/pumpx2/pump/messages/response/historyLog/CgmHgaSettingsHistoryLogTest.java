package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmHgaSettingsHistoryLogTest {
    // t:slim X2 capture: high alert threshold changed from 250 to 120 mg/dL
    @Test
    public void testThresholdChange() throws DecoderException {
        CgmHgaSettingsHistoryLog expected = new CgmHgaSettingsHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField
            446870051L, 2061L, 120, 300, true, 1
        );

        CgmHgaSettingsHistoryLog parsedRes = (CgmHgaSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "a50023b2a21a0d08000078002c01010001000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmHgaSettingsHistoryLog.ModifiedField.THRESHOLD, parsedRes.getModifiedFieldEnum());
    }

    // t:slim X2 capture: repeat duration changed from 0 to 300 minutes
    @Test
    public void testDurationChange() throws DecoderException {
        CgmHgaSettingsHistoryLog expected = new CgmHgaSettingsHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField
            446869976L, 2060L, 250, 300, true, 2
        );

        CgmHgaSettingsHistoryLog parsedRes = (CgmHgaSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "a500d8b1a21a0c080000fa002c01010002000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmHgaSettingsHistoryLog.ModifiedField.DURATION, parsedRes.getModifiedFieldEnum());
    }

    // t:slim X2 capture: alert disabled, then re-enabled 4 minutes later. Byte 15 is 0x01 in these
    // two records and is not decoded, so only the fields are compared, not a rebuilt cargo.
    @Test
    public void testEnableChange() throws DecoderException {
        CgmHgaSettingsHistoryLog disabled = (CgmHgaSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "a50092b0a21afc070000c8003c00000100000000000000000000",
                new CgmHgaSettingsHistoryLog(446869650L, 2044L, 200, 60, false, 0)
        );
        assertEquals(CgmHgaSettingsHistoryLog.ModifiedField.ENABLE, disabled.getModifiedFieldEnum());

        CgmHgaSettingsHistoryLog enabled = (CgmHgaSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "a5006eb1a21a09080000c8003c00010100000000000000000000",
                new CgmHgaSettingsHistoryLog(446869870L, 2057L, 200, 60, true, 0)
        );
        assertEquals(true, enabled.getIsEnabled());
        assertEquals(CgmHgaSettingsHistoryLog.ModifiedField.ENABLE, enabled.getModifiedFieldEnum());
    }
}
