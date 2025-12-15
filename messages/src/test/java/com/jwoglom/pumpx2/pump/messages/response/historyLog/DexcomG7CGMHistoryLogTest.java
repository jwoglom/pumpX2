package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DexcomG7CGMHistoryLogTest {
    /*
     * Bluetooth comm sent history logs are little-endian.
     * But Tandem Source appears to store/process them as big-endian, see tconnectsync code.
     * This means that history log strings between the two are not directly comparable.
     */
    @Test
    public void testDexcomG7CGMHistoryLog1() throws DecoderException {
        DexcomG7CGMHistoryLog expected = new DexcomG7CGMHistoryLog(
                // long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, int egvTimestamp, int egvInfoBitmask, int interval
                566516808L, 506706L, 0, 1, 4, 32, -61, 257, 566516805, 6625, 0
        );

        DexcomG7CGMHistoryLog parsedRes = (DexcomG7CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "8f11485cc42152bb07000000010420c30101455cc421e1190000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testDexcomG7CGMHistoryLog2() throws DecoderException {
        DexcomG7CGMHistoryLog expected = new DexcomG7CGMHistoryLog(
                // long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, int egvTimestamp, int egvInfoBitmask, int interval
                566516507L, 506696L, 0, 1, 13, 32, -59, 261, 566516504, 6625, 0
        );

        DexcomG7CGMHistoryLog parsedRes = (DexcomG7CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "8f111b5bc42148bb07000000010d20c50501185bc421e1190000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testDexcomG7CGMHistoryLog3() throws DecoderException {
        DexcomG7CGMHistoryLog expected = new DexcomG7CGMHistoryLog(
                // long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, int egvTimestamp, int egvInfoBitmask, int interval
                566516207L, 506647L, 0, 1, 20, 32, -65, 265, 566516204, 6625, 0
        );

        DexcomG7CGMHistoryLog parsedRes = (DexcomG7CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "8f11ef59c42117bb07000000011420bf0901ec59c421e1190000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testDexcomG7CGMHistoryLog4() throws DecoderException {
        DexcomG7CGMHistoryLog expected = new DexcomG7CGMHistoryLog(
                // long pumpTimeSec, long sequenceNum, int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, int egvTimestamp, int egvInfoBitmask, int interval
                566515908L, 506634L, 0, 1, 18, 32, -64, 251, 566515905, 6625, 0
        );

        DexcomG7CGMHistoryLog parsedRes = (DexcomG7CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "8f11c458c4210abb07000000011220c0fb00c158c421e1190000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
