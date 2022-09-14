package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.TempRateActivatedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class TempRateActivatedHistoryLogTest {
    @Test
    public void testTempRateActivatedHistoryLog() throws DecoderException {
        TempRateActivatedHistoryLog expected = new TempRateActivatedHistoryLog(
            // float percent, float duration, int tempRateId
        );

        TempRateActivatedHistoryLog parsedRes = (TempRateActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}