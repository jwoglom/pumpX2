package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.TempRateResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class TempRateResponseTest {
    @Test
    public void testTempRateResponseNotActive() throws DecoderException {
        TempRateResponse expected = new TempRateResponse(
            // boolean active, int percentage, long startTime, long duration
                false, 0, 0, 0
        );

        TempRateResponse parsedRes = (TempRateResponse) MessageTester.test(
                "00032b030a0000000000000000000039c6",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}