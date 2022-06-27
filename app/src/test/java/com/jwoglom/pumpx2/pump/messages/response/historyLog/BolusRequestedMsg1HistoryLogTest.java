package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusRequestedMsg1HistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class BolusRequestedMsg1HistoryLogTest {
    @Test
    public void testBolusRequestedMsg1HistoryLog1() throws DecoderException {
        BolusRequestedMsg1HistoryLog expected = new BolusRequestedMsg1HistoryLog(
            // int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio
                1036, 2, true, 0, 0, 2.1372654F, 0
        );

        BolusRequestedMsg1HistoryLog parsedRes = (BolusRequestedMsg1HistoryLog) HistoryLogMessageTester.testSingle(
                "4000a162951a87c602000c04020100000000f5c8084000000000",
                expected
        );
        //assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusRequestedMsg1HistoryLog2() throws DecoderException {
        BolusRequestedMsg1HistoryLog expected = new BolusRequestedMsg1HistoryLog(
                // int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio
                1057, 2, true, 0, 0, 1.8658969F, 0
        );

        BolusRequestedMsg1HistoryLog parsedRes = (BolusRequestedMsg1HistoryLog) HistoryLogMessageTester.testSingle(
                "40003ed7971a64d802002104020100000000b6d5ee3f00000000",
                expected
        );
        //assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusRequestedMsg1HistoryLog3() throws DecoderException {
        BolusRequestedMsg1HistoryLog expected = new BolusRequestedMsg1HistoryLog(
                // int bolusId, int bolusType, boolean correctionBolusIncluded, int carbAmount, int bg, float iob, long carbRatio
                1040, 1, false, 0, 0, 1.7629877F, 6000
        );

        BolusRequestedMsg1HistoryLog parsedRes = (BolusRequestedMsg1HistoryLog) HistoryLogMessageTester.testSingle(
                "4000c449961a3dcc0200100401000000000095a9e13f70170000",
                expected
        );
        //assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}