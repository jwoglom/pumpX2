package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusRequestedMsg3HistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class BolusRequestedMsg3HistoryLogTest {
    @Test
    public void testBolusRequestedMsg3HistoryLog() throws DecoderException {
        BolusRequestedMsg3HistoryLog expected = new BolusRequestedMsg3HistoryLog(
            // int bolusId, int spare, float foodBolusSize, float correctionBolusSize, float totalBolusSize
                1032, 0, 3.33F, 0.0F, 3.33F
        );

        BolusRequestedMsg3HistoryLog parsedRes = (BolusRequestedMsg3HistoryLog) HistoryLogMessageTester.testSingle(
                "4200214e951a8ac5020008040000b81e554000000000b81e5540",
                expected
        );
        //assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}