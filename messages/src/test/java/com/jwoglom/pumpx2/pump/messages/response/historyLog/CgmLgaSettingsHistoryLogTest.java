package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmLgaSettingsHistoryLogTest {
    // Mobi capture: low alert repeat duration changed from 0 to 15 minutes. Byte 15 is 0x01 and
    // is not decoded, so only the fields are compared, not a rebuilt cargo.
    @Test
    public void testDurationChange() throws DecoderException {
        CgmLgaSettingsHistoryLog parsedRes = (CgmLgaSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "a610b848fd1f8701000050000f00010102000000000000000000",
                new CgmLgaSettingsHistoryLog(
                    // long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField
                    536692920L, 391L, 80, 15, true, 2
                )
        );
        assertEquals(80, parsedRes.getAlertLevel());
        assertEquals(CgmLgaSettingsHistoryLog.ModifiedField.DURATION, parsedRes.getModifiedFieldEnum());
    }

    // Synthetic record: low alert threshold set to 70 mg/dL
    @Test
    public void testSyntheticRoundTrip() throws DecoderException {
        CgmLgaSettingsHistoryLog expected = new CgmLgaSettingsHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField
            500000000L, 100000L, 70, 30, true, 1
        );

        CgmLgaSettingsHistoryLog parsedRes = (CgmLgaSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                Hex.encodeHexString(expected.getCargo()),
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmLgaSettingsHistoryLog.ModifiedField.THRESHOLD, parsedRes.getModifiedFieldEnum());
    }
}
