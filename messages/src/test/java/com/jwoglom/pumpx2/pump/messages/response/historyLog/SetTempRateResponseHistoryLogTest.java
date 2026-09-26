package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetTempRateResponseHistoryLogTest {
    // Two records after TempRateActivated id 5014.
    @Test
    public void testAcceptedTempRate() throws DecoderException {
        SetTempRateResponseHistoryLog expected = (SetTempRateResponseHistoryLog) new SetTempRateResponseHistoryLog(
                // long pumpTimeSec, long sequenceNum, int status, int unknown11, int tempRateId
                591098918L, 723593L, 0, 0, 5014
        ).withHeaderHighNibble(1);

        SetTempRateResponseHistoryLog parsedRes = (SetTempRateResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "351126743b23890a0b0000009613000000000000000000000000",
                expected
        );
        assertEquals(591098918L, parsedRes.getPumpTimeSec());
        assertEquals(723593L, parsedRes.getSequenceNum());
        assertEquals(0, parsedRes.getStatus());
        assertEquals(0, parsedRes.getUnknown11());
        assertEquals(5014, parsedRes.getTempRateId());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // The app logged SetTempRateResponse status=1, tempRateId=0 for this request; no
    // TempRateActivated was written.
    @Test
    public void testRejectedTempRate() throws DecoderException {
        SetTempRateResponseHistoryLog expected = (SetTempRateResponseHistoryLog) new SetTempRateResponseHistoryLog(
                591099216L, 723628L, 1, 1, 0
        ).withHeaderHighNibble(1);

        SetTempRateResponseHistoryLog parsedRes = (SetTempRateResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "351150753b23ac0a0b0001010000000000000000000000000000",
                expected
        );
        assertEquals(1, parsedRes.getStatus());
        assertEquals(1, parsedRes.getUnknown11());
        assertEquals(0, parsedRes.getTempRateId());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testIssueExample() throws DecoderException {
        SetTempRateResponseHistoryLog expected = (SetTempRateResponseHistoryLog) new SetTempRateResponseHistoryLog(
                589527121L, 640774L, 0, 0, 1815
        ).withHeaderHighNibble(1);

        SetTempRateResponseHistoryLog parsedRes = (SetTempRateResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "35115178232306c7090000001707000000000000000000000000",
                expected
        );
        assertEquals(1815, parsedRes.getTempRateId());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
