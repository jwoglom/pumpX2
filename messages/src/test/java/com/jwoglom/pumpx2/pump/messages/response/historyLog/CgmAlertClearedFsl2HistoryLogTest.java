package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmAlertClearedFsl2HistoryLogTest {
    // SYNTHETIC record: no FreeStyle Libre 2 capture exists. It is built with the validated
    // CgmAlertClearedDex (370) layout and sensorType=2 (FSL2) at byte 11. The old uint32@10 read
    // decoded this as alertId 514 (2 + 2 << 8).
    @Test
    public void testCgmAlertClearedFsl2HistoryLog_synthetic() throws DecoderException {
        CgmAlertClearedFsl2HistoryLog expected = (CgmAlertClearedFsl2HistoryLog) new CgmAlertClearedFsl2HistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertId, int sensorType
            600000300L, 1001L, 2, 2
        ).withHeaderHighNibble(1);

        CgmAlertClearedFsl2HistoryLog parsedRes = (CgmAlertClearedFsl2HistoryLog) HistoryLogMessageTester.testSingle(
                "cd112c47c323e903000002020000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2, parsedRes.getAlertId());
        assertEquals(CGMAlertStatusResponse.CGMAlert.HIGH_CGM_ALERT, parsedRes.getAlert());
        assertEquals(2, parsedRes.getSensorType());
        assertEquals(CgmStatusV2Response.CgmSensorType.FSL2, parsedRes.getSensorTypeEnum());
    }
}
