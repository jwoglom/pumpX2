package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusPermissionRequestHistoryLogTest {
    // Mobi driven by Trio: TandemKit signs requests with its last-read pumpTimeSinceReset.
    @Test
    public void testMobiTandemKitRequest() throws DecoderException {
        BolusPermissionRequestHistoryLog expected = new BolusPermissionRequestHistoryLog(
                // long pumpTimeSec, long sequenceNum, long requestTimestamp, int headerHighNibble
                589529230L, 640842L, 195084L, 1
        );

        BolusPermissionRequestHistoryLog parsedRes = (BolusPermissionRequestHistoryLog) HistoryLogMessageTester.testSingle(
                "52118e8023234ac709000cfa0200000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(195084L, parsedRes.getRequestTimestamp());
    }

    // t:slim X2 with Tandem's Android app, whose trailer carried a pump-clock time 10 s before the record.
    @Test
    public void testX2OfficialAppRequest() throws DecoderException {
        BolusPermissionRequestHistoryLog expected = new BolusPermissionRequestHistoryLog(
                461464522L, 23532L, 461464512L
        );

        BolusPermissionRequestHistoryLog parsedRes = (BolusPermissionRequestHistoryLog) HistoryLogMessageTester.testSingle(
                "5201ca63811bec5b0000c063811b000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(461464512L, parsedRes.getRequestTimestamp());
    }
}
