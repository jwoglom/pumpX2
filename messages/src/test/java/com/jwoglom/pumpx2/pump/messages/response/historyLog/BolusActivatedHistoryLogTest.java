package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusActivatedHistoryLogTest {
    @Test
    public void testBolusActivatedHistoryLog1() throws DecoderException {
        BolusActivatedHistoryLog expected = new BolusActivatedHistoryLog(
            // long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolusSize
            446033777L, 182751L, 1037, 1, 0.0F, 1.1F
        );

        BolusActivatedHistoryLog parsedRes = (BolusActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "370071ef951adfc902000d04010000000000cdcc8c3f00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusActivatedHistoryLog2() throws DecoderException {
        BolusActivatedHistoryLog expected = new BolusActivatedHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int selectedIob, float iob, float bolusSize
                446141315L, 185926L, 1053, 1, 2.0116007F, 0.5F
        );

        BolusActivatedHistoryLog parsedRes = (BolusActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "37008393971a46d602001d04010011be00400000003f00000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
