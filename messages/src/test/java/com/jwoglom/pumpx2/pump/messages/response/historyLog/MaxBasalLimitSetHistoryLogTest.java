package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class MaxBasalLimitSetHistoryLogTest {
    @Test
    public void testMaxBasalLimitSet_mobiSameValue() throws DecoderException {
        // Maintainer's Tandem Mobi: written in the same second as a
        // SetMaxBasalLimitRequest(maxHourlyBasalMilliunits=4000) that re-sent the current limit.
        MaxBasalLimitSetHistoryLog expected = (MaxBasalLimitSetHistoryLog) new MaxBasalLimitSetHistoryLog(
                // long pumpTimeSec, long sequenceNum, long maxHourlyBasalMilliunits, long previousMaxHourlyBasalMilliunits
                590188136L, 674689L, 4000L, 4000L
        ).withHeaderHighNibble(1);

        MaxBasalLimitSetHistoryLog parsedRes = (MaxBasalLimitSetHistoryLog) HistoryLogMessageTester.testSingle(
                "1211688e2d23814b0a00a00f0000a00f00000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(4000L, parsedRes.getMaxHourlyBasalMilliunits());
        assertEquals(4000L, parsedRes.getPreviousMaxHourlyBasalMilliunits());
    }

    @Test
    public void testMaxBasalLimitSet_tslimX2Change() throws DecoderException {
        // Maintainer's t:slim X2: limit raised from the 3 U/hr default to 5 U/hr.
        MaxBasalLimitSetHistoryLog expected = new MaxBasalLimitSetHistoryLog(
                505928913L, 71L, 5000L, 3000L
        );

        MaxBasalLimitSetHistoryLog parsedRes = (MaxBasalLimitSetHistoryLog) HistoryLogMessageTester.testSingle(
                "1201d1dc271e4700000088130000b80b00000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(5000L, parsedRes.getMaxHourlyBasalMilliunits());
        assertEquals(3000L, parsedRes.getPreviousMaxHourlyBasalMilliunits());
    }
}
