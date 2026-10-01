package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DataLogCorruptionHistoryLogTest {
    /**
     * Byte layout per the Tandem Source event schema for LID_DATA_LOG_CORRUPTION: block u32 @0, reason u8 @4.
     */
    @Test
    public void testDataLogCorruptionHistoryLogLayout() throws DecoderException {
        DataLogCorruptionHistoryLog expected = new DataLogCorruptionHistoryLog(
                // long pumpTimeSec, long sequenceNum, long block, int reason
                566516808L, 506706L, 4660L, 4
        );

        DataLogCorruptionHistoryLog parsedRes = (DataLogCorruptionHistoryLog) HistoryLogMessageTester.testSingle(
                // header: 0x003C | pumpTime | seqNum || 34120000 | 04 | 00...
                "3c00485cc42152bb070034120000040000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(4660L, parsedRes.getBlock());
        assertEquals(4, parsedRes.getReason());
    }
}
