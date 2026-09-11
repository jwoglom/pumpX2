package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmAlertActivatedDexHistoryLogTest {
    @Test
    public void testCgmAlertActivatedDexHistoryLog1() throws DecoderException {
        CgmAlertActivatedDexHistoryLog expected = (CgmAlertActivatedDexHistoryLog) new CgmAlertActivatedDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long faultLocatorData, long param1, float param2
            580770552L, 490757L, 2, 3, 8468L, 208L, 200.0F
        ).withHeaderHighNibble(1);

        CgmAlertActivatedDexHistoryLog parsedRes = (CgmAlertActivatedDexHistoryLog) HistoryLogMessageTester.testSingle(
                "7111f8da9d22057d07000203000014210000d000000000004843",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testCgmAlertActivatedDexHistoryLog2() throws DecoderException {
        CgmAlertActivatedDexHistoryLog expected = (CgmAlertActivatedDexHistoryLog) new CgmAlertActivatedDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long faultLocatorData, long param1, float param2
            580720459L, 488615L, 14, 3, 8462L, 25L, 917.0F
        ).withHeaderHighNibble(1);

        CgmAlertActivatedDexHistoryLog parsedRes = (CgmAlertActivatedDexHistoryLog) HistoryLogMessageTester.testSingle(
                "71114b179d22a77407000e0300000e2100001900000000406544",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
