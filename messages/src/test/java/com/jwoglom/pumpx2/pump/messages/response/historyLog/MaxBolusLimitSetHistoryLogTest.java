package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class MaxBolusLimitSetHistoryLogTest {
    @Test
    public void testMaxBolusLimitSet_mobiSameValue() throws DecoderException {
        // Maintainer's Tandem Mobi: written in the same second as a
        // SetMaxBolusLimitRequest(maxBolusMilliunits=15000) that re-sent the current limit.
        MaxBolusLimitSetHistoryLog expected = (MaxBolusLimitSetHistoryLog) new MaxBolusLimitSetHistoryLog(
                // long pumpTimeSec, long sequenceNum, int maxBolusMilliunits, int previousMaxBolusMilliunits
                590188136L, 674692L, 15000, 15000
        ).withHeaderHighNibble(1);

        MaxBolusLimitSetHistoryLog parsedRes = (MaxBolusLimitSetHistoryLog) HistoryLogMessageTester.testSingle(
                "4611688e2d23844b0a00983a983a000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(15000, parsedRes.getMaxBolusMilliunits());
        assertEquals(15000, parsedRes.getPreviousMaxBolusMilliunits());
    }

    @Test
    public void testMaxBolusLimitSet_tslimX2Change() throws DecoderException {
        // Maintainer's t:slim X2: max bolus raised from the 10 U default to 25 U.
        MaxBolusLimitSetHistoryLog expected = new MaxBolusLimitSetHistoryLog(
                505928913L, 70L, 25000, 10000
        );

        MaxBolusLimitSetHistoryLog parsedRes = (MaxBolusLimitSetHistoryLog) HistoryLogMessageTester.testSingle(
                "4601d1dc271e46000000a8611027000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(25000, parsedRes.getMaxBolusMilliunits());
        assertEquals(10000, parsedRes.getPreviousMaxBolusMilliunits());
    }
}
