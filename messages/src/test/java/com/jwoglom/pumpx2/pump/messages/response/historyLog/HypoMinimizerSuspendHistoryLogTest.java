package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.HypoMinimizerSuspendHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class HypoMinimizerSuspendHistoryLogTest {
    @Test
    public void testHypoMinimizerSuspendHistoryLog() throws DecoderException {
        HypoMinimizerSuspendHistoryLog expected = new HypoMinimizerSuspendHistoryLog(
            // 
        );

        HypoMinimizerSuspendHistoryLog parsedRes = (HypoMinimizerSuspendHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}