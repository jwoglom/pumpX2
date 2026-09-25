package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusPermissionResponseHistoryLogTest {
    @Test
    public void testMobiPermissionGranted() throws DecoderException {
        BolusPermissionResponseHistoryLog expected = new BolusPermissionResponseHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At14, int unknownU8At15, int headerHighNibble
                589529230L, 640843L, 2904, 0, 0, 1
        );

        BolusPermissionResponseHistoryLog parsedRes = (BolusPermissionResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "29118e8023234bc70900580b0000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2904, parsedRes.getBolusId());
    }

    // t:slim X2: a request the pump refused (BolusPermissionResponse status 1, nackReason 1, bolusId 0).
    @Test
    public void testX2PermissionRefused() throws DecoderException {
        BolusPermissionResponseHistoryLog expected = new BolusPermissionResponseHistoryLog(
                461464522L, 23533L, 0, 1, 1
        );

        BolusPermissionResponseHistoryLog parsedRes = (BolusPermissionResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "2901ca63811bed5b000000000000010100000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getBolusId());
        assertEquals(1, parsedRes.getUnknownU8At14());
        assertEquals(1, parsedRes.getUnknownU8At15());
    }
}
