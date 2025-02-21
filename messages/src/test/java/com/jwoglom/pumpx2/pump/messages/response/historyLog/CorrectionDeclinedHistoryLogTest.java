package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.CorrectionDeclinedHistoryLog;

import java.util.Arrays;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class CorrectionDeclinedHistoryLogTest {
    @Test
    public void testCorrectionDeclinedHistoryLog() throws DecoderException {
        CorrectionDeclinedHistoryLog expected = new CorrectionDeclinedHistoryLog(
            // int bg, int bolusId, float iob, int targetBg, int isf
        );

        CorrectionDeclinedHistoryLog parsedRes = (CorrectionDeclinedHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}