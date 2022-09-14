package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ParamChangeGlobalSettingsHistoryLogTest {
    @Test
    public void testGlobalSettingsChangeHistoryLog() throws DecoderException {
        ParamChangeGlobalSettingsHistoryLog expected = new ParamChangeGlobalSettingsHistoryLog(
            // int modifiedData, int qbDataStatus, int qbActive, int qbDataEntryType, int qbIncrementUnits, int qbIncrementCarbs, int buttonVolume, int qbVolume, int bolusVolume, int reminderVolume, int alertVolume
        );

        ParamChangeGlobalSettingsHistoryLog parsedRes = (ParamChangeGlobalSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}