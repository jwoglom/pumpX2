package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.HypoMinimizerResumeHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class HypoMinimizerResumeHistoryLogTest {
    @Test
    public void testHypoMinimizerResumeHistoryLog() throws DecoderException {
        HypoMinimizerResumeHistoryLog expected = new HypoMinimizerResumeHistoryLog(
            // long reason
        );

        HypoMinimizerResumeHistoryLog parsedRes = (HypoMinimizerResumeHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}