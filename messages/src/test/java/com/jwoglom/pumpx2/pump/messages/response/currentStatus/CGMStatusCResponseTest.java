package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMStatusCResponseTest {
    @Test
    public void testCGMStatusCResponse_parseCargo() throws DecoderException {
        CGMStatusCResponse expected = new CGMStatusCResponse(
                0, 0, 0, 0, 0, 0, 3, false, 0, 0
        );

        CGMStatusCResponse parsedRes = new CGMStatusCResponse();
        parsedRes.parse(Hex.decodeHex("000000000000000000000000000000000000030000000000"));

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(0, parsedRes.getSessionStateId());
        assertEquals(CGMStatusCResponse.SessionState.STATUS_STOPPED, parsedRes.getSessionState());
        assertEquals(0, parsedRes.getLastCalibrationTimestamp());
        assertEquals(0, parsedRes.getSensorStartedTimestamp());
        assertEquals(0, parsedRes.getTransmitterBatteryStatusId());
        assertEquals(CGMStatusCResponse.TransmitterBatteryStatus.UNAVAILABLE, parsedRes.getTransmitterBatteryStatus());
        assertEquals(0, parsedRes.getSessionDurationSeconds());
        assertEquals(0, parsedRes.getSessionTimeRemainingSeconds());
        assertEquals(3, parsedRes.getCgmSensorTypeId());
        assertEquals(CGMStatusCResponse.CgmSensorType.DEXCOM_G7, parsedRes.getCgmSensorType());
        assertFalse(parsedRes.isGracePeriod());
        assertEquals(0, parsedRes.getSensorWarmupDurationSeconds());
        assertEquals(0, parsedRes.getRemainingWarmupDurationSeconds());
    }

    @Test
    public void testCGMStatusCResponse_activeSessionWithWarmup() throws DecoderException {
        // sessionState=2 (SESSION_ACTIVE), transmitterBatteryStatus=3 (OKAY),
        // sessionDuration=864000s (10d), sessionTimeRemaining=863100s,
        // cgmSensorType=3 (DEXCOM_G7), gracePeriod=1,
        // sensorWarmupDuration=1800s (30m), remainingWarmupDuration=900s (15m)
        CGMStatusCResponse expected = new CGMStatusCResponse(
                2, 500000000L, 500000100L, 3, 864000, 863100, 3, true, 1800, 900
        );

        CGMStatusCResponse parsedRes = new CGMStatusCResponse();
        parsedRes.parse(expected.getCargo());

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(24, parsedRes.getCargo().length);

        assertEquals(2, parsedRes.getSessionStateId());
        assertEquals(CGMStatusCResponse.SessionState.SESSION_ACTIVE, parsedRes.getSessionState());
        assertEquals(500000000L, parsedRes.getLastCalibrationTimestamp());
        assertEquals(500000100L, parsedRes.getSensorStartedTimestamp());
        assertEquals(CGMStatusCResponse.TransmitterBatteryStatus.OKAY, parsedRes.getTransmitterBatteryStatus());
        assertEquals(864000, parsedRes.getSessionDurationSeconds());
        assertEquals(863100, parsedRes.getSessionTimeRemainingSeconds());
        assertEquals(CGMStatusCResponse.CgmSensorType.DEXCOM_G7, parsedRes.getCgmSensorType());
        assertTrue(parsedRes.isGracePeriod());
        assertEquals(1800, parsedRes.getSensorWarmupDurationSeconds());
        assertEquals(900, parsedRes.getRemainingWarmupDurationSeconds());
    }

    @Test
    public void testCGMStatusCResponse_maxUint16Warmup() throws DecoderException {
        CGMStatusCResponse expected = new CGMStatusCResponse(
                1, 0, 0, 0, 0, 0, 1, false, 65535, 65535
        );

        CGMStatusCResponse parsedRes = new CGMStatusCResponse();
        parsedRes.parse(expected.getCargo());

        assertEquals(65535, parsedRes.getSensorWarmupDurationSeconds());
        assertEquals(65535, parsedRes.getRemainingWarmupDurationSeconds());
    }
}
