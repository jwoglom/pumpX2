package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BasalDeliveryHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class BasalDeliveryHistoryLogTest {
    @Test
    public void testBasalDeliveryHistoryLog() throws DecoderException {
        BasalDeliveryHistoryLog expected = new BasalDeliveryHistoryLog(
            // int commandedRateSource, int commandedRate, int profileBasalRate, int algorithmRate, int tempRate
        );

        BasalDeliveryHistoryLog parsedRes = (BasalDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}