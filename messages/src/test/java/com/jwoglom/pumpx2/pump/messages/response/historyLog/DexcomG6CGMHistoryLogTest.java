package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DexcomG6CGMHistoryLogTest {
    @Test
    public void testCGMHistoryLog1() throws DecoderException {
        DexcomG6CGMHistoryLog expected = new DexcomG6CGMHistoryLog(
            // long pumpTimeSec, long sequenceNum, int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval
                446191545L, 187445L, 0, 1, -2, 6, -89, 112, 446191545L, 481, 0
        );

        DexcomG6CGMHistoryLog parsedRes = (DexcomG6CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "0001b957981a35dc0200000001fe06a77000b957981ae1010001",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(1, parsedRes.getEgvCount());
        // 2022 firmware leaves egvInfoBitmask bits 11-13 at 0
        assertEquals(CgmStatusV2Response.CgmSensorType.NOT_APPLICABLE, parsedRes.getEgvSensorType());
    }

    @Test
    public void testCGMHistoryLog2() throws DecoderException {
        DexcomG6CGMHistoryLog expected = new DexcomG6CGMHistoryLog(
                // long pumpTimeSec, long sequenceNum, int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval
                446115345L, 184926L, 0, 1, -3, 6, -67, 81, 446115345L, 481, 0
        );

        DexcomG6CGMHistoryLog parsedRes = (DexcomG6CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "0001112e971a5ed20200000001fd06bd5100112e971ae1010001",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCGMHistoryLog3() throws DecoderException {
        DexcomG6CGMHistoryLog expected = new DexcomG6CGMHistoryLog(
                // long pumpTimeSec, long sequenceNum, int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval
                446090145L, 184327L, 0, 1, -12, 6, -82, 78, 446090145L, 481, 0
        );

        DexcomG6CGMHistoryLog parsedRes = (DexcomG6CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "0001a1cb961a07d00200000001f406ae4e00a1cb961ae1010001",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCGMHistoryLog4() throws DecoderException {
        DexcomG6CGMHistoryLog expected = new DexcomG6CGMHistoryLog(
                // long pumpTimeSec, long sequenceNum, int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval
                445997146L, 181873L, 0, 1, 13, 6, -87, 145, 445997146L, 481, 0
        );

        DexcomG6CGMHistoryLog parsedRes = (DexcomG6CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "00015a60951a71c602000000010d06a991005a60951ae1010001",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCGMHistoryLog_NoArrow() throws DecoderException {
        // a backfill record (cgmDataType 2): egvCount is 0
        DexcomG6CGMHistoryLog expected = new DexcomG6CGMHistoryLog(
                // long pumpTimeSec, long sequenceNum, int glucoseValueStatus, int cgmDataType, int rate, int algorithmState, int rssi, int currentGlucoseDisplayValue, long timeStampSeconds, int egvInfoBitmask, int interval, int egvCount
                470616768L, 957589L, 0, 2, 0, 6, -66, 167, 470616468L, 2530, 1, 0
        );

        DexcomG6CGMHistoryLog parsedRes = (DexcomG6CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "0001c00a0d1c959c0e000000020006bea70094090d1ce2090100",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getEgvCount());
        assertEquals(CgmStatusV2Response.CgmSensorType.DEXCOM_G6, parsedRes.getEgvSensorType());
    }

    @Test
    public void testCGMHistoryLog_EgvCountRoundTrip() {
        // synthetic five-minute reading covering one missed reading (egvCount 2), sensor type G6
        DexcomG6CGMHistoryLog built = new DexcomG6CGMHistoryLog(
                470616768L, 957590L, 0, 1, -3, 6, -70, 120, 470616768L, 0x09E1, 0, 2
        );
        assertEquals(26, built.getCargo().length);
        assertEquals(2, built.getCargo()[25]);

        DexcomG6CGMHistoryLog parsed = (DexcomG6CGMHistoryLog) HistoryLogParser.parse(built.getCargo());
        assertHexEquals(built.getCargo(), parsed.getCargo());
        assertEquals(2, parsed.getEgvCount());
        assertEquals(-3, parsed.getRate());
        assertEquals(-70, parsed.getRssi());
        assertEquals(CgmStatusV2Response.CgmSensorType.DEXCOM_G6, parsed.getEgvSensorType());
    }
}