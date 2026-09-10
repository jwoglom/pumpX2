package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import java.util.Set;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog.BolusSource;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog.BolusType;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusDeliveryHistoryLogTest {
    @Test
    public void testBolusDeliveryHistoryLog1() throws DecoderException {
        BolusDeliveryHistoryLog expected = new BolusDeliveryHistoryLog(
            // long pumpTimeSec, long sequenceNum, int bolusID, int bolusDeliveryStatusId, Set<BolusType> bolusTypes, BolusSource bolusSource, int remoteId, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal
                446066139L, 183687L,
                1044,
                0,
                Set.of(BolusType.NOW, BolusType.CORRECTION),
                BolusSource.CONTROL_IQ_AUTO_BOLUS,
                20,
                1001,
                0,
                1001,
                0,
                1001
        );

        BolusDeliveryHistoryLog parsedRes = (BolusDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1801db6d961a87cd0200140400090714e9030000e9030000e903",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusDeliveryHistoryLog2() throws DecoderException {
        BolusDeliveryHistoryLog expected = new BolusDeliveryHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusID, int bolusDeliveryStatusId, Set<BolusType> bolusTypes, BolusSource bolusSource, int remoteId, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal
                446175714L, 187031L,
                1063,
                0,
                Set.of(BolusType.NOW, BolusType.CORRECTION),
                BolusSource.GUI,
                39,
                210,
                0,
                210,
                0,
                210
        );

        BolusDeliveryHistoryLog parsedRes = (BolusDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "1801e219981a97da0200270400090127d2000000d2000000d200",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusDeliveryHistoryLog3() throws DecoderException {
        BolusDeliveryHistoryLog expected = new BolusDeliveryHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusID, int bolusDeliveryStatusId, Set<BolusType> bolusTypes, BolusSource bolusSource, int remoteId, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal
                446175382L, 186993L,
                1062,
                1,
                Set.of(BolusType.NOW, BolusType.CORRECTION),
                BolusSource.GUI,
                38,
                920,
                0,
                920,
                0,
                0
        );

        BolusDeliveryHistoryLog parsedRes = (BolusDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "18019618981a71da020026040109012698030000980300000000",
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // Observed on a Tandem Mobi driven by Trio (Control-IQ off, no CGM paired), Sept 2026 BLE capture.
    // Byte 15 (remoteId) equalled bolusId & 0xFF in 312/312 BolusDelivery records of that capture;
    // the three t:slim X2 fixtures above obey the same rule (1044 -> 20, 1063 -> 39, 1062 -> 38).
    // Both records below belong to bolus 2903 (0x0b57): the "started" record (status 1,
    // deliveredTotal 0) and the "completed" record (status 0, deliveredTotal 150) written 4 s later.
    @Test
    public void testBolusDeliveryHistoryLog_mobiRemoteBolusStarted() throws DecoderException {
        BolusDeliveryHistoryLog expected = (BolusDeliveryHistoryLog) new BolusDeliveryHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusID, int bolusDeliveryStatusId, Set<BolusType> bolusTypes, BolusSource bolusSource, int remoteId, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal
                589526845L, 640755L,
                2903,
                1,
                Set.of(BolusType.NOW, BolusType.OVERRIDE),
                BolusSource.BLUETOOTH_REMOTE_BOLUS,
                0x57,
                150,
                0,
                0,
                0,
                0
        ).withHeaderHighNibble(1);

        BolusDeliveryHistoryLog parsedRes = (BolusDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "18113d772323f3c60900570b0105085796000000000000000000",
                expected
        );

        assertEquals(2903, parsedRes.getBolusID());
        assertEquals(1, parsedRes.getBolusDeliveryStatusId());
        assertEquals(Set.of(BolusType.NOW, BolusType.OVERRIDE), parsedRes.getBolusTypes());
        assertEquals(BolusSource.BLUETOOTH_REMOTE_BOLUS, parsedRes.getBolusSource());
        assertEquals(0x57, parsedRes.getRemoteId());
        assertEquals(parsedRes.getBolusID() & 0xFF, parsedRes.getRemoteId());
        assertEquals(parsedRes.getRemoteId(), parsedRes.getReserved());
        assertEquals(150, parsedRes.getRequestedNow());
        assertEquals(0, parsedRes.getDeliveredTotal());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testBolusDeliveryHistoryLog_mobiRemoteBolusCompleted() throws DecoderException {
        BolusDeliveryHistoryLog expected = (BolusDeliveryHistoryLog) new BolusDeliveryHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusID, int bolusDeliveryStatusId, Set<BolusType> bolusTypes, BolusSource bolusSource, int remoteId, int requestedNow, int requestedLater, int correction, int extendedDurationRequested, int deliveredTotal
                589526849L, 640760L,
                2903,
                0,
                Set.of(BolusType.NOW, BolusType.OVERRIDE),
                BolusSource.BLUETOOTH_REMOTE_BOLUS,
                0x57,
                150,
                0,
                0,
                0,
                150
        ).withHeaderHighNibble(1);

        BolusDeliveryHistoryLog parsedRes = (BolusDeliveryHistoryLog) HistoryLogMessageTester.testSingle(
                "181141772323f8c60900570b0005085796000000000000009600",
                expected
        );

        assertEquals(2903, parsedRes.getBolusID());
        assertEquals(0, parsedRes.getBolusDeliveryStatusId());
        assertEquals(Set.of(BolusType.NOW, BolusType.OVERRIDE), parsedRes.getBolusTypes());
        assertEquals(BolusSource.BLUETOOTH_REMOTE_BOLUS, parsedRes.getBolusSource());
        assertEquals(parsedRes.getBolusID() & 0xFF, parsedRes.getRemoteId());
        assertEquals(150, parsedRes.getRequestedNow());
        assertEquals(150, parsedRes.getDeliveredTotal());
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}
