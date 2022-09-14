package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.ParamChangeRemSettingsHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ParamChangeRemSettingsHistoryLogTest {
    @Test
    public void testParamChangeRemSettingsHistoryLog() throws DecoderException {
        ParamChangeRemSettingsHistoryLog expected = new ParamChangeRemSettingsHistoryLog(
            // int modification, int status, int lowBgThreshold, int highBgThreshold, int siteChangeDays
        );

        ParamChangeRemSettingsHistoryLog parsedRes = (ParamChangeRemSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}