package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmFraSettingsHistoryLogTest {
    // t:slim X2 capture: fall rate alert threshold set to 2 mg/dL/min
    @Test
    public void testThresholdChange() throws DecoderException {
        CgmFraSettingsHistoryLog expected = new CgmFraSettingsHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertLevel, int repeatDuration, boolean isEnabled, int modifiedField
            446870630L, 2079L, 2, 0, true, 1
        );

        CgmFraSettingsHistoryLog parsedRes = (CgmFraSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "a80066b4a21a1f08000002000000010001000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmFraSettingsHistoryLog.ModifiedField.THRESHOLD, parsedRes.getModifiedFieldEnum());
    }
}
