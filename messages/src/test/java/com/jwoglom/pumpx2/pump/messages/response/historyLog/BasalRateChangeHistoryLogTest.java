package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BasalRateChangeHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class BasalRateChangeHistoryLogTest {
    @Test
    public void testBasalRateChangeHistoryLog() throws DecoderException {
        BasalRateChangeHistoryLog expected = new BasalRateChangeHistoryLog(
            // float commandBasalRate, float baseBasalRate, float maxBasalRate, int insulinDeliveryProfile, int changeType
        );

        BasalRateChangeHistoryLog parsedRes = (BasalRateChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}