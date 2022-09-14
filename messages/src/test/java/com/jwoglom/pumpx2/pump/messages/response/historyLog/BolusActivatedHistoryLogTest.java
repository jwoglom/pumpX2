package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusActivatedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusActivatedHistoryLogTest {
    @Test
    public void testBolusActivatedHistoryLog() throws DecoderException {
        BolusActivatedHistoryLog expected = new BolusActivatedHistoryLog(
            // int bolusId, float iob, float bolusSize
        );

        BolusActivatedHistoryLog parsedRes = (BolusActivatedHistoryLog) HistoryLogMessageTester.testSingle(
                "xxxx",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}