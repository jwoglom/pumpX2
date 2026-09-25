package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmOorSettingsHistoryLogTest {
    // Mobi capture: out-of-range alert (20 minutes) disabled. Byte 15 is 0x01 and is not decoded,
    // so only the fields are compared, not a rebuilt cargo.
    @Test
    public void testDisabled() throws DecoderException {
        CgmOorSettingsHistoryLog parsedRes = (CgmOorSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "a9105749fd1fb301000014000000000100000000000000000000",
                new CgmOorSettingsHistoryLog(
                    // long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField
                    536693079L, 435L, 20, 0, false, 0
                )
        );
        assertEquals(20, parsedRes.getAlertLevel());
        assertEquals(CgmOorSettingsHistoryLog.ModifiedField.ENABLE, parsedRes.getModifiedFieldEnum());
    }

    // Synthetic record: out-of-range alert time changed to 25 minutes
    @Test
    public void testSyntheticRoundTrip() throws DecoderException {
        CgmOorSettingsHistoryLog expected = new CgmOorSettingsHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField
            500000000L, 100000L, 25, 0, true, 1
        );

        CgmOorSettingsHistoryLog parsedRes = (CgmOorSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                Hex.encodeHexString(expected.getCargo()),
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmOorSettingsHistoryLog.ModifiedField.THRESHOLD, parsedRes.getModifiedFieldEnum());
    }
}
