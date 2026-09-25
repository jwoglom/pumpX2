package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmRraSettingsHistoryLogTest {
    // Mobi capture: rise rate alert threshold changed from 3 to 2 mg/dL/min
    @Test
    public void testThresholdChange() throws DecoderException {
        CgmRraSettingsHistoryLog expected = (CgmRraSettingsHistoryLog) new CgmRraSettingsHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField
            536692989L, 414L, 2, 0, true, 1
        ).withHeaderHighNibble(1);

        CgmRraSettingsHistoryLog parsedRes = (CgmRraSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "a710fd48fd1f9e01000002000000010001000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmRraSettingsHistoryLog.ModifiedField.THRESHOLD, parsedRes.getModifiedFieldEnum());
    }

    // Mobi capture: rise rate alert then disabled
    @Test
    public void testDisabled() throws DecoderException {
        CgmRraSettingsHistoryLog expected = (CgmRraSettingsHistoryLog) new CgmRraSettingsHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField
            536693002L, 419L, 2, 0, false, 0
        ).withHeaderHighNibble(1);

        CgmRraSettingsHistoryLog parsedRes = (CgmRraSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "a7100a49fd1fa301000002000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(false, parsedRes.getIsEnabled());
        assertEquals(CgmRraSettingsHistoryLog.ModifiedField.ENABLE, parsedRes.getModifiedFieldEnum());
    }
}
