package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.ControlIQPcmChangeHistoryLog;

import java.util.Arrays;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class ControlIQPcmChangeHistoryLogTest {
    @Test
    public void testControlIQPcmChangeHistoryLog() throws DecoderException {
        ControlIQPcmChangeHistoryLog expected = new ControlIQPcmChangeHistoryLog(
            // int currentPcm, int previousPcm
        );

        ControlIQPcmChangeHistoryLog parsedRes = (ControlIQPcmChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}