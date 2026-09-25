package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlRequestHistoryLogTest {
    // Tandem Mobi driven by Trio: TempRateCompleted, then this record for StopTempRateRequest (166).
    // requestTimestamp 1764775 is the pumpTimeSinceReset of the TimeSinceResetResponse TandemKit
    // had received before signing the request.
    @Test
    public void testStopTempRateRequest() throws DecoderException {
        ControlRequestHistoryLog expected = (ControlRequestHistoryLog) new ControlRequestHistoryLog(
                // long pumpTimeSec, long sequenceNum, long requestTimestamp, int requestOpCode
                591098918L, 723589L, 1764775L, 166
        ).withHeaderHighNibble(1);

        ControlRequestHistoryLog parsedRes = (ControlRequestHistoryLog) HistoryLogMessageTester.testSingle(
                "221126743b23850a0b00a7ed1a00a60000000000000000000000",
                expected
        );
        assertEquals(591098918L, parsedRes.getPumpTimeSec());
        assertEquals(723589L, parsedRes.getSequenceNum());
        assertEquals(1764775L, parsedRes.getRequestTimestamp());
        assertEquals(166, parsedRes.getRequestOpCode());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // SetTempRateRequest (164) of the same cycle, signed with the same trailer time.
    @Test
    public void testSetTempRateRequest() throws DecoderException {
        ControlRequestHistoryLog expected = (ControlRequestHistoryLog) new ControlRequestHistoryLog(
                591098918L, 723592L, 1764775L, 164
        ).withHeaderHighNibble(1);

        ControlRequestHistoryLog parsedRes = (ControlRequestHistoryLog) HistoryLogMessageTester.testSingle(
                "221126743b23880a0b00a7ed1a00a40000000000000000000000",
                expected
        );
        assertEquals(164, parsedRes.getRequestOpCode());
        assertEquals((int) (byte) -92 & 0xFF, parsedRes.getRequestOpCode());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testEnterFillTubingModeRequest() throws DecoderException {
        ControlRequestHistoryLog expected = (ControlRequestHistoryLog) new ControlRequestHistoryLog(
                590440422L, 689018L, 1105602L, 148
        ).withHeaderHighNibble(1);

        ControlRequestHistoryLog parsedRes = (ControlRequestHistoryLog) HistoryLogMessageTester.testSingle(
                "2211e66731237a830a00c2de1000940000000000000000000000",
                expected
        );
        assertEquals(148, parsedRes.getRequestOpCode());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // Tandem's official Mobi app signs with the phone's UTC time in seconds since 2008, so the
    // value is the record's pump-local time plus the UTC offset (18002 s here).
    @Test
    public void testOfficialAppUserInteractionRequest() throws DecoderException {
        ControlRequestHistoryLog expected = (ControlRequestHistoryLog) new ControlRequestHistoryLog(
                536692298L, 236L, 536710300L, 132
        ).withHeaderHighNibble(1);

        ControlRequestHistoryLog parsedRes = (ControlRequestHistoryLog) HistoryLogMessageTester.testSingle(
                "22114a46fd1fec0000009c8cfd1f840000000000000000000000",
                expected
        );
        assertEquals(536710300L, parsedRes.getRequestTimestamp());
        assertEquals(132, parsedRes.getRequestOpCode());
        assertEquals(18002L, parsedRes.getRequestTimestamp() - parsedRes.getPumpTimeSec());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // t:slim X2 record (header high nibble 0).
    @Test
    public void testTslimX2UserInteractionRequest() throws DecoderException {
        ControlRequestHistoryLog expected = new ControlRequestHistoryLog(
                534182906L, 1049379L, 534200996L, 132
        );

        ControlRequestHistoryLog parsedRes = (ControlRequestHistoryLog) HistoryLogMessageTester.testSingle(
                "2201fafbd61f23031000a442d71f840000000000000000000000",
                expected
        );
        assertEquals(0, parsedRes.getHeaderHighNibble());
        assertEquals(132, parsedRes.getRequestOpCode());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // Example from the issue: bytes 10-13 are one uint32 (192683), not a uint16 and two bytes.
    @Test
    public void testIssueExample() throws DecoderException {
        ControlRequestHistoryLog expected = (ControlRequestHistoryLog) new ControlRequestHistoryLog(
                589527121L, 640770L, 192683L, 166
        ).withHeaderHighNibble(1);

        ControlRequestHistoryLog parsedRes = (ControlRequestHistoryLog) HistoryLogMessageTester.testSingle(
                "22115178232302c70900abf00200a60000000000000000000000",
                expected
        );
        assertEquals(192683L, parsedRes.getRequestTimestamp());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
