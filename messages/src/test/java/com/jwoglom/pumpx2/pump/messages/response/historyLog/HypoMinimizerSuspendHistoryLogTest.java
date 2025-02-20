package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.HypoMinimizerSuspendHistoryLog;

import java.util.Arrays;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
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