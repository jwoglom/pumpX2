package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusCompletedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class BolusCompletedHistoryLogTest {
    @Test
    @Ignore("float parsing is broken")
    public void testBolusCompletedHistoryLog() throws DecoderException {
        BolusCompletedHistoryLog expected = new BolusCompletedHistoryLog(
            // int completionStatus, int bolusId, float iob, float insulinDelivered, float insulinRequested
            3, 1057, 0.0F, 0.0F, 0.0F
        );

        BolusCompletedHistoryLog parsedRes = (BolusCompletedHistoryLog) HistoryLogMessageTester.testSingle(
                "14009ed7971a70d802000300210454c86940f2bae43ff2bae43f",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}