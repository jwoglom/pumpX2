package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmAlertClearedDexHistoryLogTest {
    @Test
    public void testCgmAlertClearedDexHistoryLog1() throws DecoderException {
        CgmAlertClearedDexHistoryLog expected = (CgmAlertClearedDexHistoryLog) new CgmAlertClearedDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertId, int sensorType
            580777763L, 491134L, 2, 3
        ).withHeaderHighNibble(1);

        CgmAlertClearedDexHistoryLog parsedRes = (CgmAlertClearedDexHistoryLog) HistoryLogMessageTester.testSingle(
                "721123f79d227e7e070002030000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmStatusV2Response.CgmSensorType.DEXCOM_G7, parsedRes.getSensorTypeEnum());
    }

    @Test
    public void testCgmAlertClearedDexHistoryLog2() throws DecoderException {
        CgmAlertClearedDexHistoryLog expected = (CgmAlertClearedDexHistoryLog) new CgmAlertClearedDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertId, int sensorType
            580720754L, 488623L, 14, 3
        ).withHeaderHighNibble(1);

        CgmAlertClearedDexHistoryLog parsedRes = (CgmAlertClearedDexHistoryLog) HistoryLogMessageTester.testSingle(
                "721172189d22af7407000e030000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmStatusV2Response.CgmSensorType.DEXCOM_G7, parsedRes.getSensorTypeEnum());
    }
}
