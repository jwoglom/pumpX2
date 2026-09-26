package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ControlResponseHistoryLogTest {
    @Test
    public void testStopTempRateResponse() throws DecoderException {
        ControlResponseHistoryLog expected = (ControlResponseHistoryLog) new ControlResponseHistoryLog(
                // long pumpTimeSec, long sequenceNum, int responseOpCode, int status
                591098918L, 723590L, 167, 0
        ).withHeaderHighNibble(1);

        ControlResponseHistoryLog parsedRes = (ControlResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "2c1126743b23860a0b00a7000000000000000000000000000000",
                expected
        );
        assertEquals(591098918L, parsedRes.getPumpTimeSec());
        assertEquals(723590L, parsedRes.getSequenceNum());
        assertEquals(167, parsedRes.getResponseOpCode());
        assertEquals(0, parsedRes.getStatus());
        assertEquals(0, parsedRes.getUnknown11());
        assertArrayEquals(new byte[13], parsedRes.getUnknownTail());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // The app logged ResumePumpingResponse status=1 for this request and status=0 for its retry.
    @Test
    public void testResumePumpingResponseFailed() throws DecoderException {
        ControlResponseHistoryLog expected = (ControlResponseHistoryLog) new ControlResponseHistoryLog(
                590519594L, 692817L, 155, 1
        ).withHeaderHighNibble(1);

        ControlResponseHistoryLog parsedRes = (ControlResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "2c112a9d322351920a009b000100000000000000000000000000",
                expected
        );
        assertEquals(155, parsedRes.getResponseOpCode());
        assertEquals(1, parsedRes.getStatus());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testSuspendPumpingResponseFailed() throws DecoderException {
        ControlResponseHistoryLog expected = (ControlResponseHistoryLog) new ControlResponseHistoryLog(
                590614626L, 697678L, 157, 1
        ).withHeaderHighNibble(1);

        ControlResponseHistoryLog parsedRes = (ControlResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "2c11621034234ea50a009d000100000000000000000000000000",
                expected
        );
        assertEquals(157, parsedRes.getResponseOpCode());
        assertEquals(1, parsedRes.getStatus());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // t:slim X2 UserInteractionResponse record: bytes 15-17 hold the low 24 bits of pumpTimeSec.
    @Test
    public void testTslimX2UserInteractionResponseKeepsTailBytes() throws DecoderException {
        byte[] tail = Hex.decodeHex("0000fafbd60000000000000000");
        ControlResponseHistoryLog expected = new ControlResponseHistoryLog(
                // long pumpTimeSec, long sequenceNum, int responseOpCode, int unknown11, int status, byte[] unknownTail
                534182906L, 1049380L, 133, 0, 0, tail
        );

        ControlResponseHistoryLog parsedRes = (ControlResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "2c01fafbd61f240310008500000000fafbd60000000000000000",
                expected
        );
        assertEquals(133, parsedRes.getResponseOpCode());
        assertEquals(0, parsedRes.getStatus());
        assertArrayEquals(tail, parsedRes.getUnknownTail());
        assertEquals(parsedRes.getPumpTimeSec() & 0xFFFFFF,
                (parsedRes.getUnknownTail()[2] & 0xFF) | (parsedRes.getUnknownTail()[3] & 0xFF) << 8 | (parsedRes.getUnknownTail()[4] & 0xFF) << 16);
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testIssueExample() throws DecoderException {
        ControlResponseHistoryLog expected = (ControlResponseHistoryLog) new ControlResponseHistoryLog(
                589527121L, 640771L, 167, 0
        ).withHeaderHighNibble(1);

        ControlResponseHistoryLog parsedRes = (ControlResponseHistoryLog) HistoryLogMessageTester.testSingle(
                "2c115178232303c70900a7000000000000000000000000000000",
                expected
        );
        assertEquals(167, parsedRes.getResponseOpCode());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
