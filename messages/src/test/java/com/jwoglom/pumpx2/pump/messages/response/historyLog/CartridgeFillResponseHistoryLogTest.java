package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

// Records from the maintainer's Tandem Mobi pumps: a Sept 2026 cartridge change driven by
// Trio/TandemKit, and a July 2025 official-app capture where ExitChangeCartridgeMode was answered
// with status 1.
public class CartridgeFillResponseHistoryLogTest {
    private static CartridgeFillResponseHistoryLog build(long pumpTimeSec, long sequenceNum, int responseOpCode, int status) {
        return (CartridgeFillResponseHistoryLog) new CartridgeFillResponseHistoryLog(
                pumpTimeSec, sequenceNum, responseOpCode, status
        ).withHeaderHighNibble(1);
    }

    private static CartridgeFillResponseHistoryLog check(String hex, long pumpTimeSec, long sequenceNum, int responseOpCode, int status) throws DecoderException {
        CartridgeFillResponseHistoryLog expected = build(pumpTimeSec, sequenceNum, responseOpCode, status);
        CartridgeFillResponseHistoryLog parsed = (CartridgeFillResponseHistoryLog) HistoryLogMessageTester.testSingle(hex, expected);
        assertEquals(pumpTimeSec, parsed.getPumpTimeSec());
        assertEquals(sequenceNum, parsed.getSequenceNum());
        assertEquals(responseOpCode, parsed.getResponseOpCode());
        assertEquals(status, parsed.getStatus());
        assertEquals(0, parsed.getUnknown11());
        assertArrayEquals(new byte[13], parsed.getUnknownTail());
        assertHexEquals(expected.getCargo(), parsed.getCargo());
        return parsed;
    }

    @Test
    public void testExitChangeCartridgeModeResponse() throws DecoderException {
        check("6711e267312376830a0093000000000000000000000000000000", 590440418L, 689014L, 147, 0);
    }

    @Test
    public void testEnterFillTubingModeResponse() throws DecoderException {
        check("6711e66731237b830a0095000000000000000000000000000000", 590440422L, 689019L, 149, 0);
    }

    @Test
    public void testFillCannulaResponse() throws DecoderException {
        check("671154683123b4830a0099000000000000000000000000000000", 590440532L, 689076L, 153, 0);
    }

    @Test
    public void testExitChangeCartridgeModeResponseFailed() throws DecoderException {
        check("6711f325f0201c62090093000100000000000000000000000000", 552609267L, 614940L, 147, 1);
    }

    @Test
    public void testUnknownBytesRoundTrip() {
        byte[] tail = new byte[13];
        tail[0] = 0x12;
        tail[12] = 0x34;
        CartridgeFillResponseHistoryLog built = new CartridgeFillResponseHistoryLog(1L, 2L, 145, 7, 0, tail);
        CartridgeFillResponseHistoryLog parsed = new CartridgeFillResponseHistoryLog();
        parsed.parse(built.getCargo());
        assertEquals(145, parsed.getResponseOpCode());
        assertEquals(7, parsed.getUnknown11());
        assertEquals(0, parsed.getStatus());
        assertArrayEquals(tail, parsed.getUnknownTail());
        assertHexEquals(built.getCargo(), parsed.getCargo());
    }
}
