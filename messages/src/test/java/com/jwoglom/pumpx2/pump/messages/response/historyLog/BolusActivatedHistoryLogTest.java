package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusActivatedHistoryLog;

import com.google.common.collect.ImmutableList;
import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;
public class BolusActivatedHistoryLogTest {
    @Test
    public void testBolusActivatedHistoryLog1() throws DecoderException {
        BolusActivatedHistoryLog expected = new BolusActivatedHistoryLog(
            // int bolusId, float iob, float bolusSize
            1037, 0.0F, 1.1F

        );

        BolusActivatedHistoryLog parsedRes = (BolusActivatedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "370071ef951adfc902000d04010000000000cdcc8c3f00000000",
                expected
        );
    }

    @Test
    public void testBolusActivatedHistoryLog2() throws DecoderException {
        BolusActivatedHistoryLog expected = new BolusActivatedHistoryLog(
                // int bolusId, float iob, float bolusSize
                1053, 2.0116007F, 0.5F

        );

        BolusActivatedHistoryLog parsedRes = (BolusActivatedHistoryLog) HistoryLogMessageTester.testSingleIgnoringBaseFields(
                "37008393971a46d602001d04010011be00400000003f00000000",
                expected
        );
    }
}