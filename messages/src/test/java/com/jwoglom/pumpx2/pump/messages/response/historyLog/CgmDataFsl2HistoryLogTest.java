package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.TreeSet;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmDataFsl2HistoryLogTest {
    @Test
    public void testCgmDataFsl2HistoryLogParse() {
        CgmDataFsl2HistoryLog expected = new CgmDataFsl2HistoryLog(
                // long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, long egvTimestamp, int egvInfoBitmaskRaw, int interval
                566516808L, 506706L, 0, 1, -3, 100, -61, 257, 566516805L, 0x10E1, 5
        );

        HistoryLog parsed = HistoryLogParser.parse(expected.getCargo());
        assertTrue(parsed instanceof CgmDataFsl2HistoryLog);

        CgmDataFsl2HistoryLog parsedRes = (CgmDataFsl2HistoryLog) parsed;
        assertEquals(expected.getPumpTimeSec(), parsedRes.getPumpTimeSec());
        assertEquals(expected.getSequenceNum(), parsedRes.getSequenceNum());
        assertEquals(0, parsedRes.getGlucoseValueStatusRaw());
        assertEquals(CgmDataFsl2HistoryLog.GlucoseValueStatus.PRECISE_VALUE, parsedRes.getGlucoseValueStatus());
        assertEquals(1, parsedRes.getCgmDataTypeRaw());
        assertEquals(new TreeSet<>(Arrays.asList(CgmDataFsl2HistoryLog.CgmDataType.FIVE_MINUTE_READING)), parsedRes.getCgmDataType());
        assertEquals(-3, parsedRes.getRate());
        assertEquals(100, parsedRes.getAlgorithmStateRaw());
        assertEquals(CgmDataFsl2HistoryLog.AlgorithmState.OK, parsedRes.getAlgorithmState());
        assertEquals(-61, parsedRes.getRssi());
        assertEquals(257, parsedRes.getCurrentGlucoseDisplayValue());
        assertEquals(566516805L, parsedRes.getEgvTimestamp());
        assertEquals(0x10E1, parsedRes.getEgvInfoBitmaskRaw());
        assertEquals(2, parsedRes.getSensorType());
        assertEquals(new TreeSet<>(Arrays.asList(
                CgmDataFsl2HistoryLog.EgvInfo.FIVE_MINUTE_READING,
                CgmDataFsl2HistoryLog.EgvInfo.VALID_TIMESTAMP,
                CgmDataFsl2HistoryLog.EgvInfo.VALID_EGV_RANGE,
                CgmDataFsl2HistoryLog.EgvInfo.VALID_ALG_STATE)), parsedRes.getEgvInfo());
        assertEquals(5, parsedRes.getInterval());
    }

    /**
     * Byte layout per the Tandem Source event schema for LID_CGM_DATA_FSL2:
     * status u8 @0, data type u8 @1, rate s16 @2, algorithm state u8 @4, rssi s8 @5,
     * value u16 @6, timestamp u32 @8, EGV info u16 @12, interval u8 @14.
     */
    @Test
    public void testCgmDataFsl2HistoryLogLayout() throws DecoderException {
        CgmDataFsl2HistoryLog expected = new CgmDataFsl2HistoryLog(
                566516808L, 506706L, 1, 32, -2, 101, -59, 261, 566516504L, 0x11E1, 1
        );

        CgmDataFsl2HistoryLog parsedRes = (CgmDataFsl2HistoryLog) HistoryLogMessageTester.testSingle(
                // header: 0x0174 | pumpTime | seqNum || 01 20 | feff | 65 | c5 | 0501 | 185cc421 | e111 | 01 | 00
                "7401485cc42152bb07000120feff65c50501185cc421e1110100",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmDataFsl2HistoryLog.GlucoseValueStatus.SPECIAL_HIGH, parsedRes.getGlucoseValueStatus());
        assertEquals(new TreeSet<>(Arrays.asList(CgmDataFsl2HistoryLog.CgmDataType.ONE_MINUTE_READING)), parsedRes.getCgmDataType());
        assertEquals(-2, parsedRes.getRate());
        assertEquals(CgmDataFsl2HistoryLog.AlgorithmState.RF_ERROR, parsedRes.getAlgorithmState());
        assertEquals(-59, parsedRes.getRssi());
        assertEquals(261, parsedRes.getCurrentGlucoseDisplayValue());
        assertEquals(566516504L, parsedRes.getEgvTimestamp());
        assertEquals(2, parsedRes.getSensorType());
        assertEquals(1, parsedRes.getInterval());
    }
}
