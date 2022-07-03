package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentEGVGuiDataResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentEGVGuiDataResponseTest {
    @Test
    public void testCurrentEGVGuiDataResponseNoCGM() throws DecoderException {
        CurrentEGVGuiDataResponse expected = new CurrentEGVGuiDataResponse(
            // long bgReadingTimestampSeconds, int cgmReading, int egvStatus, int trendRate
                0L, 0, CurrentEGVGuiDataResponse.EGVStatus.UNAVAILABLE.id(), 0
        );

        CurrentEGVGuiDataResponse parsedRes = (CurrentEGVGuiDataResponse) MessageTester.test(
                "000323030800000000000004007b52",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}