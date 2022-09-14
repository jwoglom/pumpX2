package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.ControlIQUserModeChangeHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore("needs historyLog sample")
public class ControlIQUserModeChangeHistoryLogTest {
    @Test
    public void testControlIQUserModeChangeHistoryLog() throws DecoderException {
        ControlIQUserModeChangeHistoryLog expected = new ControlIQUserModeChangeHistoryLog(
            // int currentUserMode, int previousUserMode
        );

        ControlIQUserModeChangeHistoryLog parsedRes = (ControlIQUserModeChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}