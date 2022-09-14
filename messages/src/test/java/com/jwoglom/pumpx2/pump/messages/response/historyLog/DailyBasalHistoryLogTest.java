package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.DailyBasalHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class DailyBasalHistoryLogTest {
    @Test
    public void testDailyBasalHistoryLog() throws DecoderException {
        DailyBasalHistoryLog expected = new DailyBasalHistoryLog(
            // float dailyTotalBasal, float lastBasalRate, float iob, int actualBatteryCharge, int lipoMv
        );

        DailyBasalHistoryLog parsedRes = (DailyBasalHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}