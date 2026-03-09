package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CgmStatusV2ResponseTest {
    @Test
    public void testCgmStatusV2Response_parseCargo() throws DecoderException {
        CgmStatusV2Response expected = new CgmStatusV2Response(
                0, 0, 0, 0, 0, 0, 3, false
        );

        CgmStatusV2Response parsedRes = new CgmStatusV2Response();
        parsedRes.parse(Hex.decodeHex("0000000000000000000000000000000000000300"));

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(0, parsedRes.getSessionStateId());
        assertEquals(CgmStatusV2Response.SessionState.STATUS_STOPPED, parsedRes.getSessionState());
        assertEquals(0, parsedRes.getLastCalibrationTimestamp());
        assertEquals(0, parsedRes.getSensorStartedTimestamp());
        assertEquals(0, parsedRes.getTransmitterBatteryStatusId());
        assertEquals(CgmStatusV2Response.TransmitterBatteryStatus.UNAVAILABLE, parsedRes.getTransmitterBatteryStatus());
        assertEquals(0, parsedRes.getSessionDurationSeconds());
        assertEquals(0, parsedRes.getSessionTimeRemainingSeconds());
        assertEquals(3, parsedRes.getCgmSensorTypeId());
        assertEquals(CgmStatusV2Response.CgmSensorType.DEXCOM_G7, parsedRes.getCgmSensorType());
        assertFalse(parsedRes.isGracePeriod());
    }
}
