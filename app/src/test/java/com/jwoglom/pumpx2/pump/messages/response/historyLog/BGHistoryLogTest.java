package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BGHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class BGHistoryLogTest {
    @Test
    @Ignore("float parsing for IOB is incorrect")
    public void testBGHistoryLog() throws DecoderException {
        BGHistoryLog expected = new BGHistoryLog(
            // int bg, int cgmCalibration, int bgSource, float iob, int targetBG, int isf, long spare
                new byte[]{8,90,-106,26,-19,-52,2,0},
                162, 0, 1, Float.NaN, 110, 30, 1
        );

        BGHistoryLog parsedRes = (BGHistoryLog) HistoryLogMessageTester.testSingle(
                "1000085a961aedcc0200a20000015c8f2e416e001e0001000000",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}