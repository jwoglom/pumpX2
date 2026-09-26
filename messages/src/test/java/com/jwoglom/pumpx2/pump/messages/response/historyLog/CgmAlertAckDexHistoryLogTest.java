package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CgmStatusV2Response;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmAlertAckDexHistoryLogTest {
    @Test
    public void testCgmAlertAckDexHistoryLog1() throws DecoderException {
        CgmAlertAckDexHistoryLog expected = (CgmAlertAckDexHistoryLog) new CgmAlertAckDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long ackSource
            580750504L, 489859L, 2, 3, 0L
        ).withHeaderHighNibble(1);

        CgmAlertAckDexHistoryLog parsedRes = (CgmAlertAckDexHistoryLog) HistoryLogMessageTester.testSingle(
                "7311a88c9d228379070002030000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmStatusV2Response.CgmSensorType.DEXCOM_G7, parsedRes.getSensorTypeEnum());
        assertEquals(CgmAlertAckDexHistoryLog.AckSource.USER, parsedRes.getAckSourceEnum());
    }

    @Test
    public void testCgmAlertAckDexHistoryLog2() throws DecoderException {
        // ackSource=1 (byte 14): acknowledged by software rather than by the user
        CgmAlertAckDexHistoryLog expected = (CgmAlertAckDexHistoryLog) new CgmAlertAckDexHistoryLog(
            // long pumpTimeSec, long sequenceNum, int alertId, int sensorType, long ackSource
            579789268L, 449784L, 32, 3, 1L
        ).withHeaderHighNibble(1);

        CgmAlertAckDexHistoryLog parsedRes = (CgmAlertAckDexHistoryLog) HistoryLogMessageTester.testSingle(
                "7311d4e18e22f8dc060020030000010000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CgmStatusV2Response.CgmSensorType.DEXCOM_G7, parsedRes.getSensorTypeEnum());
        assertEquals(CgmAlertAckDexHistoryLog.AckSource.SOFTWARE, parsedRes.getAckSourceEnum());
    }

    @Test
    public void testCgmAlertAckDexHistoryLog_enumMapping() {
        assertEquals(CgmStatusV2Response.CgmSensorType.DEXCOM_G6, new CgmAlertAckDexHistoryLog(2, 1, 0L).getSensorTypeEnum());
        assertEquals(CgmStatusV2Response.CgmSensorType.NOT_APPLICABLE, new CgmAlertAckDexHistoryLog(2, 0, 0L).getSensorTypeEnum());
        assertEquals(CgmAlertAckDexHistoryLog.AckSource.USER, CgmAlertAckDexHistoryLog.AckSource.fromId(0));
        assertEquals(CgmAlertAckDexHistoryLog.AckSource.SOFTWARE, CgmAlertAckDexHistoryLog.AckSource.fromId(1));
        assertEquals(null, CgmAlertAckDexHistoryLog.AckSource.fromId(2));
    }
}
