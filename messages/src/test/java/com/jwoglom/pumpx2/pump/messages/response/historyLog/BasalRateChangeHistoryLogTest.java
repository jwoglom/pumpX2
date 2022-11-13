package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BasalRateChangeHistoryLogTest {
    @Test
    public void testBasalRateChangeHistoryLog_Default() throws DecoderException {
        BasalRateChangeHistoryLog expected = new BasalRateChangeHistoryLog(
            // float commandBasalRate, float baseBasalRate, float maxBasalRate, int insulinDeliveryProfile, int changeType
                0.436F, 0.8F, 5.0F, 0, 1
        );

        BasalRateChangeHistoryLog parsedRes = (BasalRateChangeHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "0300ce53951ac7c50200643bdf3ecdcc4c3f0000a04000000100",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBasalRateChangeHistoryLog_PumpSuspended() throws DecoderException {
        BasalRateChangeHistoryLog expected = new BasalRateChangeHistoryLog(
                // float commandBasalRate, float baseBasalRate, float maxBasalRate, int insulinDeliveryProfile, int changeType
                0.0F, 1.25F, 5.0F, 0, 16
        );

        BasalRateChangeHistoryLog parsedRes = (BasalRateChangeHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "03002fe5951a6bc90200000000000000a03f0000a04000001000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}