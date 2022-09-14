package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.AlertActivatedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AlertActivatedHistoryLogTest {
    @Test
    public void testAlertActivatedHistoryLog() throws DecoderException {
        AlertActivatedHistoryLog expected = new AlertActivatedHistoryLog(
            // long alertId
        );

        AlertActivatedHistoryLog parsedRes = (AlertActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}