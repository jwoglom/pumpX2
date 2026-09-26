package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlRequestCountersHistoryLogTest {
    @Test
    public void testControlRequestCounters_mobi() throws DecoderException {
        // Maintainer's Tandem Mobi. In the two hours before this record TandemKit sent 35 CONTROL
        // requests: 14 SetTempRate, 14 StopTempRate, 3 BolusPermission, 3 InitiateBolus and 1 DismissNotification.
        ControlRequestCountersHistoryLog expected = (ControlRequestCountersHistoryLog) new ControlRequestCountersHistoryLog(
                // long pumpTimeSec, long sequenceNum, long controlRequestCount, long unknown14, long unknown18, long unknown22
                590119274L, 671193L, 35L, 0L, 34L, 1L
        ).withHeaderHighNibble(1);

        ControlRequestCountersHistoryLog parsedRes = (ControlRequestCountersHistoryLog) HistoryLogMessageTester.testSingle(
                "ec116a812c23d93d0a0023000000000000002200000001000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(35L, parsedRes.getControlRequestCount());
        assertEquals(parsedRes.getControlRequestCount(), parsedRes.getUnknown18() + parsedRes.getUnknown22());
    }

    @Test
    public void testControlRequestCounters_issueExamples() throws DecoderException {
        ControlRequestCountersHistoryLog first = (ControlRequestCountersHistoryLog) HistoryLogMessageTester.testSingle(
                "ec112a7f23233dc7090025000000000000002200000003000000",
                new ControlRequestCountersHistoryLog(589528874L, 640829L, 37L, 0L, 34L, 3L).withHeaderHighNibble(1)
        );
        assertEquals(first.getControlRequestCount(), first.getUnknown18() + first.getUnknown22());

        ControlRequestCountersHistoryLog second = (ControlRequestCountersHistoryLog) HistoryLogMessageTester.testSingle(
                "ec11aaef232365cc090028000000000000001f00000009000000",
                new ControlRequestCountersHistoryLog(589557674L, 642149L, 40L, 0L, 31L, 9L).withHeaderHighNibble(1)
        );
        assertEquals(second.getControlRequestCount(), second.getUnknown18() + second.getUnknown22());
    }
}
