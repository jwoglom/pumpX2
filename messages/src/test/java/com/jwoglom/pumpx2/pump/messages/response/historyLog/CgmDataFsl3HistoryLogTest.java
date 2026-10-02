package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.TreeSet;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmDataFsl3HistoryLogTest {
    @Test
    public void testCgmDataFsl3HistoryLogParse() {
        CgmDataFsl3HistoryLog expected = new CgmDataFsl3HistoryLog(
                // long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, long egvTimestamp, int egvInfoBitmaskRaw, int interval
                566516507L, 506696L, 0, 1, 13, 100, -59, 261, 566516504L, 0x20E1, 5
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmDataFsl3HistoryLog);

        CgmDataFsl3HistoryLog parsedRes = (CgmDataFsl3HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(0, parsedRes.getGlucoseValueStatusRaw());
        assertEquals(CgmDataFsl2HistoryLog.GlucoseValueStatus.PRECISE_VALUE, parsedRes.getGlucoseValueStatus());
        assertEquals(new TreeSet<>(Arrays.asList(CgmDataFsl2HistoryLog.CgmDataType.FIVE_MINUTE_READING)), parsedRes.getCgmDataType());
        assertEquals(13, parsedRes.getRate());
        assertEquals(CgmDataFsl2HistoryLog.AlgorithmState.OK, parsedRes.getAlgorithmState());
        assertEquals(-59, parsedRes.getRssi());
        assertEquals(261, parsedRes.getCurrentGlucoseDisplayValue());
        assertEquals(566516504L, parsedRes.getEgvTimestamp());
        assertEquals(0x20E1, parsedRes.getEgvInfoBitmaskRaw());
        assertEquals(4, parsedRes.getSensorType());
        assertEquals(5, parsedRes.getInterval());
    }

    @Test
    public void testCgmDataFsl3HistoryLogLayout() throws DecoderException {
        CgmDataFsl3HistoryLog expected = new CgmDataFsl3HistoryLog(
                566516507L, 506696L, 2, 2, 7, 100, -70, 60, 566516504L, 0x20E2, 1
        );

        CgmDataFsl3HistoryLog parsedRes = (CgmDataFsl3HistoryLog) HistoryLogMessageTester.testSingle(
                // header: 0x01E0 | pumpTime | seqNum || 02 02 | 0700 | 64 | ba | 3c00 | 185bc421 | e220 | 01 | 00
                "e0011b5bc42148bb07000202070064ba3c00185bc421e2200100",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmDataFsl2HistoryLog.GlucoseValueStatus.SPECIAL_LOW, parsedRes.getGlucoseValueStatus());
        assertEquals(new TreeSet<>(Arrays.asList(CgmDataFsl2HistoryLog.CgmDataType.BACKFILL)), parsedRes.getCgmDataType());
        assertEquals(7, parsedRes.getRate());
        assertEquals(-70, parsedRes.getRssi());
        assertEquals(60, parsedRes.getCurrentGlucoseDisplayValue());
        assertEquals(4, parsedRes.getSensorType());
    }
}
