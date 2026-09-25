package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMAlertStatusResponse.CGMAlert;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmAlertAckHistoryLogTest {
    // t:slim X2 capture: acknowledges a CGM low alert that was activated 336 seconds earlier
    @Test
    public void testLowAlertAck() throws DecoderException {
        CgmAlertAckHistoryLog expected = new CgmAlertAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            446055384L, 183295L, 3L
        );

        CgmAlertAckHistoryLog parsedRes = (CgmAlertAckHistoryLog) HistoryLogMessageTester.testSingle(
                "ad00d843961affcb020003000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CGMAlert.LOW_CGM_ALERT, parsedRes.getAlert());
    }

    @Test
    public void testHighAlertAck() throws DecoderException {
        CgmAlertAckHistoryLog expected = new CgmAlertAckHistoryLog(
            // long pumpTimeSec, long sequenceNum, long alertId
            446158873L, 186494L, 2L
        );

        CgmAlertAckHistoryLog parsedRes = (CgmAlertAckHistoryLog) HistoryLogMessageTester.testSingle(
                "ad0019d8971a7ed8020002000000000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(CGMAlert.HIGH_CGM_ALERT, parsedRes.getAlert());
    }

    // The ack and the CgmAlertCleared record for the same alert are written in the same second.
    @Test
    public void testSensorExpiringAckFollowedByCleared() throws DecoderException {
        CgmAlertAckHistoryLog ack = (CgmAlertAckHistoryLog) HistoryLogMessageTester.testSingle(
                "ad0052f4981a5ce002000c000000000000000000000000000000",
                new CgmAlertAckHistoryLog(446231634L, 188508L, 12L)
        );
        assertEquals(CGMAlert.SENSOR_EXPIRING_CGM_ALERT, ack.getAlert());

        CgmAlertClearedHistoryLog cleared = (CgmAlertClearedHistoryLog) HistoryLogParser.parse(
                Hex.decodeHex("ac0052f4981a5de002000c000000000000000000000000000000"));
        assertEquals(ack.getAlertId(), cleared.getAlertId());
        assertEquals(ack.getPumpTimeSec(), cleared.getPumpTimeSec());
        assertEquals(ack.getSequenceNum() + 1, cleared.getSequenceNum());
    }
}
