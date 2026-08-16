package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ParamChangeRemSettingsHistoryLogTest {
    // parse() skips raw bytes 12-13 between `status` and `lowBgThreshold`; across all six
    // captured samples for this type those two bytes are 0x0000, so there is no evidence they
    // carry meaningful unparsed data in this capture.
    @Test
    public void testParamChangeRemSettingsHistoryLog1() throws DecoderException {
        ParamChangeRemSettingsHistoryLog expected = (ParamChangeRemSettingsHistoryLog) new ParamChangeRemSettingsHistoryLog(
            // long pumpTimeSec, long sequenceNum, int modification, int status, int lowBgThreshold, int highBgThreshold, int siteChangeDays
            580601544L, 483934L, 2, 4, 70, 200, 2
        ).withHeaderHighNibble(1);

        ParamChangeRemSettingsHistoryLog parsedRes = (ParamChangeRemSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "6110c8469b225e620700020400004600c8000200000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testParamChangeRemSettingsHistoryLog2() throws DecoderException {
        ParamChangeRemSettingsHistoryLog expected = (ParamChangeRemSettingsHistoryLog) new ParamChangeRemSettingsHistoryLog(
            // long pumpTimeSec, long sequenceNum, int modification, int status, int lowBgThreshold, int highBgThreshold, int siteChangeDays
            579744851L, 448177L, 2, 4, 70, 200, 2
        ).withHeaderHighNibble(1);

        ParamChangeRemSettingsHistoryLog parsedRes = (ParamChangeRemSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "611053348e22b1d60600020400004600c8000200000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
