package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AuthorizationCountersHistoryLogTest {
    @Test
    public void testAuthorizationCounters_threeReconnects() throws DecoderException {
        // Maintainer's Tandem Mobi. In the two hours before this record TandemKit reconnected three
        // times, receiving 3 Jpake3SessionKeyResponses and 3 Jpake4KeyConfirmationResponses.
        AuthorizationCountersHistoryLog expected = (AuthorizationCountersHistoryLog) new AuthorizationCountersHistoryLog(
                // long pumpTimeSec, long sequenceNum, long authorizationRequestCount, long unknown14, long unknown18, long unknown22
                590119270L, 671183L, 6L, 0L, 0L, 0L
        ).withHeaderHighNibble(1);

        AuthorizationCountersHistoryLog parsedRes = (AuthorizationCountersHistoryLog) HistoryLogMessageTester.testSingle(
                "ee1166812c23cf3d0a0006000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(6L, parsedRes.getAuthorizationRequestCount());
    }

    @Test
    public void testAuthorizationCounters_issueExampleZero() throws DecoderException {
        AuthorizationCountersHistoryLog parsedRes = (AuthorizationCountersHistoryLog) HistoryLogMessageTester.testSingle(
                "ee11237f232333c7090000000000000000000000000000000000",
                new AuthorizationCountersHistoryLog(589528867L, 640819L, 0L, 0L, 0L, 0L).withHeaderHighNibble(1)
        );
        assertEquals(0L, parsedRes.getAuthorizationRequestCount());
    }
}
