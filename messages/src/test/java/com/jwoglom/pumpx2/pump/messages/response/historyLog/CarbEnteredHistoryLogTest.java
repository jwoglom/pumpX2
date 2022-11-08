package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.CarbEnteredHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
public class CarbEnteredHistoryLogTest {
    @Test
    public void testCarbEnteredHistoryLog1() throws DecoderException {
        CarbEnteredHistoryLog expected = new CarbEnteredHistoryLog(
            // float carbs
                45.0F
        );

        CarbEnteredHistoryLog parsedRes = (CarbEnteredHistoryLog) HistoryLogMessageTester.testSingle(
                "3000cea4971ad1d6020000003442000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
    @Test
    public void testCarbEnteredHistoryLog2() throws DecoderException {
        CarbEnteredHistoryLog expected = new CarbEnteredHistoryLog(
                // float carbs
                60.0F
        );

        CarbEnteredHistoryLog parsedRes = (CarbEnteredHistoryLog) HistoryLogMessageTester.testSingle(
                "3000c6e2981ad7df020000007042000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}