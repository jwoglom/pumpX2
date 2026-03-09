package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMStatusResponseTest {
    @Test
    public void testCGMStatusResponse_parseCargo() throws DecoderException {
        CGMStatusResponse expected = new CGMStatusResponse(
            // int sessionState, long lastCalibrationTimestamp, long sensorStartedTimestamp, int transmitterBatteryStatus
                0, 0L, 0L, 0
        );

        CGMStatusResponse parsedRes = new CGMStatusResponse();
        parsedRes.parse(Hex.decodeHex("00000000000000000000"));

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getSessionStateId());
        assertEquals(CGMStatusResponse.SessionState.SESSION_STOPPED, parsedRes.getSessionState());
        assertEquals(0, parsedRes.getLastCalibrationTimestamp());
        assertEquals(0, parsedRes.getSensorStartedTimestamp());
        assertEquals(0, parsedRes.getTransmitterBatteryStatusId());
        assertEquals(CGMStatusResponse.TransmitterBatteryStatus.UNAVAILABLE, parsedRes.getTransmitterBatteryStatus());
    }
}
