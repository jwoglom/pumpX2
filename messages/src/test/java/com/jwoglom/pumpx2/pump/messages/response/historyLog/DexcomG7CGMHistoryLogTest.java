package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("need sample")

public class DexcomG7CGMHistoryLogTest {
    @Test
    public void testDexcomG7CGMHistoryLogHistoryLog() throws DecoderException {
        DexcomG7CGMHistoryLog expected = new DexcomG7CGMHistoryLog(
            // int glucoseValueStatusRaw, int cgmDataTypeRaw, int rate, int algorithmStateRaw, int rssi, int currentGlucoseDisplayValue, int egvTimestamp, int egvInfoBitmask, int interval
        );

        DexcomG7CGMHistoryLog parsedRes = (DexcomG7CGMHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}