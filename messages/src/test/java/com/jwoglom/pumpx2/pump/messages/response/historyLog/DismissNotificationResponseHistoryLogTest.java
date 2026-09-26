package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DismissNotificationResponseHistoryLogTest {
    // Mobi, Trio: the second of two DismissNotificationRequests sent in the same second was
    // answered with status 1, and the record carries that status.
    @Test
    public void testDismissNotificationResponseFailedStatus() throws DecoderException {
        DismissNotificationResponseHistoryLog expected = (DismissNotificationResponseHistoryLog) new DismissNotificationResponseHistoryLog(
                // long pumpTimeSec, long sequenceNum, int responseOpCode, int status
                590519595L, 692824L, 185, 1
        ).withHeaderHighNibble(1);

        DismissNotificationResponseHistoryLog parsedRes = (DismissNotificationResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "4e112b9d322358920a00b9000100000000000000000000000000",
                expected
        );
        assertEquals(590519595L, parsedRes.getPumpTimeSec());
        assertEquals(692824L, parsedRes.getSequenceNum());
        assertEquals(185, parsedRes.getResponseOpCode());
        assertEquals(1, parsedRes.getStatus());
        assertEquals(0, parsedRes.getUnknown11());
        assertArrayEquals(new byte[13], parsedRes.getUnknownTail());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testDismissNotificationResponseSuccess() throws DecoderException {
        DismissNotificationResponseHistoryLog expected = (DismissNotificationResponseHistoryLog) new DismissNotificationResponseHistoryLog(
                // long pumpTimeSec, long sequenceNum, int responseOpCode, int status
                589632874L, 646347L, 185, 0
        ).withHeaderHighNibble(1);

        DismissNotificationResponseHistoryLog parsedRes = (DismissNotificationResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "4e116a152523cbdc0900b9000000000000000000000000000000",
                expected
        );
        assertEquals(185, parsedRes.getResponseOpCode());
        assertEquals(0, parsedRes.getStatus());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // Mobi, official app: DismissNotificationResponse status 1 in the live capture.
    @Test
    public void testDismissNotificationResponseFailedStatusOfficialApp() throws DecoderException {
        DismissNotificationResponseHistoryLog expected = (DismissNotificationResponseHistoryLog) new DismissNotificationResponseHistoryLog(
                // long pumpTimeSec, long sequenceNum, int responseOpCode, int status
                539916890L, 122665L, 185, 1
        ).withHeaderHighNibble(1);

        DismissNotificationResponseHistoryLog parsedRes = (DismissNotificationResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "4e115a7a2e2029df0100b9000100000000000000000000000000",
                expected
        );
        assertEquals(1, parsedRes.getStatus());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
