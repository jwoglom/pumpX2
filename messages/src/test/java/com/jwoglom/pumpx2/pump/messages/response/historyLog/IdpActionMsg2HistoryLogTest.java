package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class IdpActionMsg2HistoryLogTest {
    @Test
    public void testIdpActionmsg2HistoryLog() throws DecoderException {
        IdpActionMsg2HistoryLog expected = new IdpActionMsg2HistoryLog(
            // int idp, String name
        );

        IdpActionMsg2HistoryLog parsedRes = (IdpActionMsg2HistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}