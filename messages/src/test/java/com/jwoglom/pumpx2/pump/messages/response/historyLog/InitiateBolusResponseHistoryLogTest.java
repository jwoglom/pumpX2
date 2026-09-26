package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class InitiateBolusResponseHistoryLogTest {
    @Test
    public void testMobiBolusAccepted() throws DecoderException {
        InitiateBolusResponseHistoryLog expected = new InitiateBolusResponseHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At14, int unknownU8At15, int headerHighNibble
                589529231L, 640852L, 2904, 0, 0, 1
        );

        InitiateBolusResponseHistoryLog parsedRes = (InitiateBolusResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "2a118f80232354c70900580b0000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2904, parsedRes.getBolusId());
    }

    // t:slim X2: InitiateBolusRequest for bolus 10681 after the pump UI had revoked its permission;
    // the captured InitiateBolusResponse was status 1, statusTypeId 2.
    @Test
    public void testX2BolusRejected() throws DecoderException {
        InitiateBolusResponseHistoryLog expected = new InitiateBolusResponseHistoryLog(
                463684121L, 58327L, 10681, 2, 1
        );

        InitiateBolusResponseHistoryLog parsedRes = (InitiateBolusResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "2a011942a31bd7e30000b9290000020100000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2, parsedRes.getUnknownU8At14());
        assertEquals(1, parsedRes.getUnknownU8At15());
    }
}
