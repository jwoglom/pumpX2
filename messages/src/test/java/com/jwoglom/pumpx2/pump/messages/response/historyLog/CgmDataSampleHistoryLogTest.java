package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.CgmDataSampleHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmDataSampleHistoryLogTest {
    @Test
    public void testCgmDataSampleHistoryLog() throws DecoderException {
        CgmDataSampleHistoryLog expected = new CgmDataSampleHistoryLog(
            // int status, int value
        );

        CgmDataSampleHistoryLog parsedRes = (CgmDataSampleHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}