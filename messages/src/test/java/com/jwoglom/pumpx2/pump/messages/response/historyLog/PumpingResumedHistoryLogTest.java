package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.PumpingResumedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
public class PumpingResumedHistoryLogTest {
    @Test
    public void testPumpingResumedHistoryLog() throws DecoderException {
        // 0.180u
        PumpingResumedHistoryLog expected = new PumpingResumedHistoryLog(
            // int insulinAmount
                180
        );

        PumpingResumedHistoryLog parsedRes = (PumpingResumedHistoryLog) HistoryLogMessageTester.testSingle(
                "0c005fea951aa4c9020064000000b40000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}