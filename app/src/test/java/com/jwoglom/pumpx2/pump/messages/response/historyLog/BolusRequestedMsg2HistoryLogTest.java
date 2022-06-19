package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusRequestedMsg2HistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusRequestedMsg2HistoryLogTest {
    @Test
    public void testBolusRequestedMsg2HistoryLog1() throws DecoderException {
        BolusRequestedMsg2HistoryLog expected = new BolusRequestedMsg2HistoryLog(
            // int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2
            1028, 0,100, 0, 0, 30, 110, true, false, 1, 0

        );

        BolusRequestedMsg2HistoryLog parsedRes = (BolusRequestedMsg2HistoryLog) HistoryLogMessageTester.testSingle(
                "410006d5941a8cc2020004040064000000001e006e0001000100",
                expected
        );
    }

    @Test
    public void testBolusRequestedMsg2HistoryLog2() throws DecoderException {
        BolusRequestedMsg2HistoryLog expected = new BolusRequestedMsg2HistoryLog(
                // int bolusId, int options, int standardPercent, int duration, int spare1, int isf, int targetBG, boolean userOverride, boolean declinedCorrection, int selectedIOB, int spare2
                1026, 0,100, 0, 0, 30, 110, false, false, 1, 0

        );

        BolusRequestedMsg2HistoryLog parsedRes = (BolusRequestedMsg2HistoryLog) HistoryLogMessageTester.testSingle(
                "41006aa1941a31c1020002040064000000001e006e0000000100",
                expected
        );
    }
}