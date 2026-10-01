package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class VersionInfoHistoryLogTest {
    /**
     * Byte layout per the Tandem Source event schema for LID_VERSION_INFO:
     * version u32 @0, configABits u32 @4, configBBits u32 @8, armCrc u16 @12.
     */
    @Test
    public void testVersionInfoHistoryLogLayout() throws DecoderException {
        VersionInfoHistoryLog expected = new VersionInfoHistoryLog(
                // long pumpTimeSec, long sequenceNum, long version, long configABits, long configBBits, int armCrc
                566516808L, 506706L, 905914L, 0x5BL, 0x1L, 0x100A
        );

        VersionInfoHistoryLog parsedRes = (VersionInfoHistoryLog) HistoryLogMessageTester.testSingle(
                // header: 0x00BF | pumpTime | seqNum || bad20d00 | 5b000000 | 01000000 | 0a10 | 0000
                "bf00485cc42152bb0700bad20d005b000000010000000a100000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(905914L, parsedRes.getVersion());
        assertEquals(0x5BL, parsedRes.getConfigABits());
        assertEquals(0x1L, parsedRes.getConfigBBits());
        assertEquals(0x100A, parsedRes.getArmCrc());
    }
}
