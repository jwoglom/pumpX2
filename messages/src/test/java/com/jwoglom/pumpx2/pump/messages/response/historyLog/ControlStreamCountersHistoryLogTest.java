package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlStreamCountersHistoryLogTest {
    @Test
    public void testControlStreamCounters_cartridgeChange() throws DecoderException {
        // Maintainer's Tandem Mobi. The two hours before this record included a cartridge change:
        // TandemKit received 19 CONTROL_STREAM notifications (fill tubing, detecting cartridge, fill cannula...).
        ControlStreamCountersHistoryLog expected = (ControlStreamCountersHistoryLog) new ControlStreamCountersHistoryLog(
                // long pumpTimeSec, long sequenceNum, long controlStreamMessageCount, long unknown14, long unknown18, long unknown22
                590443275L, 689216L, 19L, 0L, 19L, 0L
        ).withHeaderHighNibble(1);

        ControlStreamCountersHistoryLog parsedRes = (ControlStreamCountersHistoryLog) HistoryLogMessageTester.testSingle(
                "ed110b73312340840a0013000000000000001300000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(19L, parsedRes.getControlStreamMessageCount());
    }

    @Test
    public void testControlStreamCounters_issueExampleZero() throws DecoderException {
        ControlStreamCountersHistoryLog parsedRes = (ControlStreamCountersHistoryLog) HistoryLogMessageTester.testSingle(
                "ed112a7f23233ec7090000000000000000000000000000000000",
                new ControlStreamCountersHistoryLog(589528874L, 640830L, 0L, 0L, 0L, 0L).withHeaderHighNibble(1)
        );
        assertEquals(0L, parsedRes.getControlStreamMessageCount());
    }
}
