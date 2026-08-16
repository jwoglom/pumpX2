package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class NewDayHistoryLogTest {
    @Test
    public void testNewDayHistoryLog1() throws DecoderException {
        NewDayHistoryLog expected = new NewDayHistoryLog(
            // long pumpTimeSec, long sequenceNum, float commandedBasalRate, long featuresBitmask, long featureBitmaskIndex
            580521600L, 480602L, 3.4590001F, 1986368786L, 93L
        );

        NewDayHistoryLog parsedRes = (NewDayHistoryLog) HistoryLogMessageTester.testSingle(
                "5a10800e9a225a55070042605d40129565765d00000000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }

    @Test
    public void testNewDayHistoryLog2() throws DecoderException {
        NewDayHistoryLog expected = new NewDayHistoryLog(
            // long pumpTimeSec, long sequenceNum, float commandedBasalRate, long featuresBitmask, long featureBitmaskIndex
            580694400L, 487757L, 0.0F, 1986368786L, 93L
        );

        NewDayHistoryLog parsedRes = (NewDayHistoryLog) HistoryLogMessageTester.testSingle(
                "5a1080b19c224d71070000000000129565765d00000000000000",
                expected
        );
        // no cargo round-trip: capture carries header high nibble 1 which buildCargo does not reproduce
    }
}
