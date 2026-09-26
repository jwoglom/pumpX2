package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.BolusPermissionChangeReasonResponse.ChangeReason;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BolusPermissionChangeHistoryLogTest {
    // Mobi, BLE bolus 2904: the grant record written with the BolusPermissionResponse.
    @Test
    public void testMobiBleBolusGranted() throws DecoderException {
        BolusPermissionChangeHistoryLog expected = new BolusPermissionChangeHistoryLog(
                // long pumpTimeSec, long sequenceNum, int bolusId, int unknownU8At12, int unknownU8At13, int changeReasonId, int headerHighNibble
                589529230L, 640841L, 2904, 1, 0, 0, 1
        );

        BolusPermissionChangeHistoryLog parsedRes = (BolusPermissionChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "27118e80232349c70900580b0100000000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(2904, parsedRes.getBolusId());
        assertEquals(1, parsedRes.getUnknownU8At12());
        assertEquals(0, parsedRes.getUnknownU8At13());
        assertEquals(ChangeReason.GRANTED, parsedRes.getChangeReason());
    }

    // Mobi, BLE bolus 2904: the release record written after InitiateBolusRequest was accepted.
    @Test
    public void testMobiBleBolusReleased() throws DecoderException {
        BolusPermissionChangeHistoryLog expected = new BolusPermissionChangeHistoryLog(
                589529231L, 640848L, 2904, 0, 1, 1, 1
        );

        BolusPermissionChangeHistoryLog parsedRes = (BolusPermissionChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "27118f80232350c70900580b0001010000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getUnknownU8At12());
        assertEquals(1, parsedRes.getUnknownU8At13());
        assertEquals(ChangeReason.RELEASED, parsedRes.getChangeReason());
    }

    // t:slim X2, bolus 10681: a BLE permission that the pump UI pre-empted. In the same capture
    // BolusPermissionChangeReasonResponse for this id reported lastChangeReasonId 2.
    @Test
    public void testX2BlePermissionRevokedByPumpUi() throws DecoderException {
        BolusPermissionChangeHistoryLog expected = new BolusPermissionChangeHistoryLog(
                463684113L, 58319L, 10681, 0, 1, 2
        );

        BolusPermissionChangeHistoryLog parsedRes = (BolusPermissionChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "27011142a31bcfe30000b9290001020000000000000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(10681, parsedRes.getBolusId());
        assertEquals(ChangeReason.REVOKED_PRIORITY, parsedRes.getChangeReason());
    }

    // t:slim X2, bolus 9712 requested on the pump screen (BolusDelivery bolusSource 1): grant then release.
    @Test
    public void testX2PumpUiBolusGrantedAndReleased() throws DecoderException {
        BolusPermissionChangeHistoryLog granted = new BolusPermissionChangeHistoryLog(
                497746006L, 1903680L, 9712, 3, 0, 0
        );
        BolusPermissionChangeHistoryLog parsedGranted = (BolusPermissionChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "27015600ab1d400c1d00f0250300000000000000000000000000",
                granted
        );
        assertHexEquals(granted.getCargo(), parsedGranted.getCargo());
        assertEquals(3, parsedGranted.getUnknownU8At12());

        BolusPermissionChangeHistoryLog released = new BolusPermissionChangeHistoryLog(
                497746007L, 1903682L, 9712, 0, 3, 1
        );
        BolusPermissionChangeHistoryLog parsedReleased = (BolusPermissionChangeHistoryLog) HistoryLogMessageTester.testSingle(
                "27015700ab1d420c1d00f0250003010000000000000000000000",
                released
        );
        assertHexEquals(released.getCargo(), parsedReleased.getCargo());
        assertEquals(3, parsedReleased.getUnknownU8At13());
        assertEquals(ChangeReason.RELEASED, parsedReleased.getChangeReason());
    }
}
