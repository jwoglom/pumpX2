package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class ParamChangePumpSettingsHistoryLogTest {
    @Test
    public void testPumpSettingsParameterChangeHistoryLog() throws DecoderException {
        ParamChangePumpSettingsHistoryLog expected = new ParamChangePumpSettingsHistoryLog(
            // int modification, int status, int lowInsulinThreshold, int cannulaPrimeSize, int isFeatureLocked, int autoShutdownEnabled, int oledTimeout, int autoShutdownDuration
        );

        ParamChangePumpSettingsHistoryLog parsedRes = (ParamChangePumpSettingsHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}