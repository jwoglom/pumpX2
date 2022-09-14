package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.CgmDataGxHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmDataGxHistoryLogTest {
    @Test
    public void testCgmDataGxHistoryLog() throws DecoderException {
        CgmDataGxHistoryLog expected = new CgmDataGxHistoryLog(
            // int status, int type, int rate, int rssi, int value, long timestamp, long transmitterTimestamp
        );

        CgmDataGxHistoryLog parsedRes = (CgmDataGxHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}