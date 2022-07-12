package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class BolexCompletedHistoryLogTest {
    @Test
    @Ignore("no samples to test")
    public void testBolexCompletedHistoryLog() throws DecoderException {
        BolexCompletedHistoryLog expected = new BolexCompletedHistoryLog(
            // int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested
        );

        BolexCompletedHistoryLog parsedRes = (BolexCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}