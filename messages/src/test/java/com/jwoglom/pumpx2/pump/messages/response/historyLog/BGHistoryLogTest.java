package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.time.Instant;

public class BGHistoryLogTest {
    @Test
    public void testBGHistoryLog() throws DecoderException {
        BGHistoryLog expected = new BGHistoryLog(
            // int bg, int cgmCalibration, int bgSource, float iob, int targetBG, int isf, long spare
                446061064L,
                183533L,
                162, 0, 1, 10.91F, 110, 30, 1
        );

        BGHistoryLog parsedRes = (BGHistoryLog) HistoryLogMessageTester.testSingle(
                "1000085a961aedcc0200a20000015c8f2e416e001e0001000000",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(Instant.parse("2022-02-18T17:51:04Z"), parsedRes.getPumpTimeSecInstant());
    }
}