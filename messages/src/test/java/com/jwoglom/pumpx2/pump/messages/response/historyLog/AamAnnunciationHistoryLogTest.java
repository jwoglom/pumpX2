package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class AamAnnunciationHistoryLogTest {
    // Mobi: written in the same second as the AlertActivated record for alert 26.
    @Test
    public void testAamAnnunciationMinBasalAlert() throws DecoderException {
        AamAnnunciationHistoryLog expected = (AamAnnunciationHistoryLog) new AamAnnunciationHistoryLog(
                // long pumpTimeSec, long sequenceNum, int categoryId, int unknown11, int unknown12, long notificationId, int unknown18, int unknown19
                591099614L, 723640L, 3, 1, 0, 26, 4, 3
        ).withHeaderHighNibble(1);

        AamAnnunciationHistoryLog parsedRes = (AamAnnunciationHistoryLog) HistoryLogMessageTester.testSingle(
                "b211de763b23b80a0b00030100001a0000000403000000000000",
                expected
        );
        assertEquals(591099614L, parsedRes.getPumpTimeSec());
        assertEquals(723640L, parsedRes.getSequenceNum());
        assertEquals(3, parsedRes.getCategoryId());
        assertEquals(AamAnnunciationHistoryLog.AamCategory.ALERT, parsedRes.getCategory());
        assertEquals(26L, parsedRes.getNotificationId());
        assertEquals(AlertStatusResponse.AlertResponseType.MIN_BASAL_ALERT2, parsedRes.getAlertResponseType());
        assertNull(parsedRes.getAlarmResponseType());
        assertEquals(1, parsedRes.getUnknown11());
        assertEquals(0, parsedRes.getUnknown12());
        assertEquals(4, parsedRes.getUnknown18());
        assertEquals(3, parsedRes.getUnknown19());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testAamAnnunciationLowInsulinAlertIsIdZero() throws DecoderException {
        AamAnnunciationHistoryLog expected = (AamAnnunciationHistoryLog) new AamAnnunciationHistoryLog(
                590177860L, 674178L, 3, 1, 0, 0, 4, 3
        ).withHeaderHighNibble(1);

        AamAnnunciationHistoryLog parsedRes = (AamAnnunciationHistoryLog) HistoryLogMessageTester.testSingle(
                "b21144662d2382490a0003010000000000000403000000000000",
                expected
        );
        assertEquals(0L, parsedRes.getNotificationId());
        assertEquals(AlertStatusResponse.AlertResponseType.LOW_INSULIN_ALERT, parsedRes.getAlertResponseType());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testAamAnnunciationResumePumpAlarm() throws DecoderException {
        AamAnnunciationHistoryLog expected = (AamAnnunciationHistoryLog) new AamAnnunciationHistoryLog(
                590519377L, 692809L, 4, 3, 10, 18, 6, 0
        ).withHeaderHighNibble(1);

        AamAnnunciationHistoryLog parsedRes = (AamAnnunciationHistoryLog) HistoryLogMessageTester.testSingle(
                "b211519c322349920a0004030a00120000000600000000000000",
                expected
        );
        assertEquals(AamAnnunciationHistoryLog.AamCategory.ALARM, parsedRes.getCategory());
        assertEquals(18L, parsedRes.getNotificationId());
        assertEquals(AlarmStatusResponse.AlarmResponseType.RESUME_PUMP_ALARM, parsedRes.getAlarmResponseType());
        assertNull(parsedRes.getAlertResponseType());
        assertEquals(10, parsedRes.getUnknown12());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testAamAnnunciationResumePumpAlarm2() throws DecoderException {
        AamAnnunciationHistoryLog expected = (AamAnnunciationHistoryLog) new AamAnnunciationHistoryLog(
                590930784L, 714786L, 4, 3, 0, 23, 6, 0
        ).withHeaderHighNibble(1);

        AamAnnunciationHistoryLog parsedRes = (AamAnnunciationHistoryLog) HistoryLogMessageTester.testSingle(
                "b21160e3382322e80a0004030000170000000600000000000000",
                expected
        );
        assertEquals(AlarmStatusResponse.AlarmResponseType.RESUME_PUMP_ALARM2, parsedRes.getAlarmResponseType());
        assertEquals(0, parsedRes.getUnknown12());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // t:slim X2 (header high nibble 0): a CGM alert annunciation.
    @Test
    public void testAamAnnunciationCgmAlertX2() throws DecoderException {
        AamAnnunciationHistoryLog expected = new AamAnnunciationHistoryLog(
                534250647L, 1053230L, 6, 1, 40, 14, 14, 3
        );

        AamAnnunciationHistoryLog parsedRes = (AamAnnunciationHistoryLog) HistoryLogMessageTester.testSingle(
                "b2019704d81f2e121000060128000e0000000e03000000000000",
                expected
        );
        assertEquals(AamAnnunciationHistoryLog.AamCategory.CGM_ALERT, parsedRes.getCategory());
        assertEquals(14L, parsedRes.getNotificationId());
        assertNull(parsedRes.getAlertResponseType());
        assertNull(parsedRes.getAlarmResponseType());
        assertEquals(40, parsedRes.getUnknown12());
        assertEquals(14, parsedRes.getUnknown18());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testAamCategoryFromIdUnknown() {
        assertNull(AamAnnunciationHistoryLog.AamCategory.fromId(5));
        assertEquals(AamAnnunciationHistoryLog.AamCategory.CGM_ALERT, AamAnnunciationHistoryLog.AamCategory.fromId(6));
    }
}
