package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class IdpBolusHistoryLogTest {
    @Test
    public void testBolusHistoryLog() throws DecoderException {
        IdpBolusHistoryLog expected = new IdpBolusHistoryLog(
            // int idp, int modification, int bolusStatus, int insulinDuration, int maxBolusSize, int bolusEntryType
        );

        IdpBolusHistoryLog parsedRes = (IdpBolusHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}