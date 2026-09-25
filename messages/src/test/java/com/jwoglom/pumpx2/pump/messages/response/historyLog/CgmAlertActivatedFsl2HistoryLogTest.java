package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmAlertActivatedFsl2HistoryLogTest {
    // SYNTHETIC record: no FreeStyle Libre 2 capture exists. It is built with the validated
    // CgmAlertActivatedDex (369) layout and sensorType=2 (FSL2) at byte 11.
    @Test
    public void testCgmAlertActivatedFsl2HistoryLog_synthetic() throws DecoderException {
        CgmAlertActivatedFsl2HistoryLog expected = (CgmAlertActivatedFsl2HistoryLog) new CgmAlertActivatedFsl2HistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long faultLocatorData, long param1, float param2
            600000000L, 1000L, 2, 2, 8468L, 250L, 200.0F
        ).withHeaderHighNibble(1);

        CgmAlertActivatedFsl2HistoryLog parsedRes = (CgmAlertActivatedFsl2HistoryLog) HistoryLogMessageTester.testSingle(
                "cc110046c323e80300000202000014210000fa00000000004843",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2, parsedRes.getAlertId());
        assertEquals(CGMAlertStatusResponse.CGMAlert.HIGH_CGM_ALERT, parsedRes.getAlert());
        assertEquals(2, parsedRes.getSensorType());
        assertEquals(CgmStatusV2Response.CgmSensorType.FSL2, parsedRes.getSensorTypeEnum());
        assertEquals(8468L, parsedRes.getFaultLocatorData());
        assertEquals(250L, parsedRes.getParam1());
        assertEquals(200.0F, parsedRes.getParam2(), 0.0F);
    }

    // SYNTHETIC record, as above. The old uint32@10 read decoded this as alertId 515 (3 + 2 << 8).
    @Test
    public void testCgmAlertActivatedFsl2HistoryLog_sensorTypeDoesNotLeakIntoAlertId() throws DecoderException {
        CgmAlertActivatedFsl2HistoryLog expected = (CgmAlertActivatedFsl2HistoryLog) new CgmAlertActivatedFsl2HistoryLog(
            // long pumpTimeSec, long sequenceNum, CGMAlert alert, int sensorType, long faultLocatorData, long param1, float param2
            600000000L, 1000L, CGMAlertStatusResponse.CGMAlert.LOW_CGM_ALERT, 2, 8466L, 70L, 80.0F
        ).withHeaderHighNibble(1);

        CgmAlertActivatedFsl2HistoryLog parsedRes = (CgmAlertActivatedFsl2HistoryLog) HistoryLogMessageTester.testSingle(
                "cc110046c323e80300000302000012210000460000000000a042",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(3, parsedRes.getAlertId());
        assertEquals(CGMAlertStatusResponse.CGMAlert.LOW_CGM_ALERT, parsedRes.getAlert());
        assertEquals(CgmStatusV2Response.CgmSensorType.FSL2, parsedRes.getSensorTypeEnum());
    }
}
