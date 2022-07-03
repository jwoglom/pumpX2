package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMStatusResponseTest {
    @Test
    public void testCGMStatusResponse() throws DecoderException {
        CGMStatusResponse expected = new CGMStatusResponse(
            // int sessionState, long lastCalibrationTimestamp, long sensorStartedTimestamp, int transmitterBatteryStatus
                0, 0L, 0L, 0
        );

        CGMStatusResponse parsedRes = (CGMStatusResponse) MessageTester.test(
                "000351030a00000000000000000000a926",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}