package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBasalStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentBasalStatusResponseTest {
    @Test
    public void testCurrentBasalStatusResponse() throws DecoderException {
        CurrentBasalStatusResponse expected = new CurrentBasalStatusResponse(
            // long profileBasalRate, long currentBasalRate, int basalModifiedBitmask
                800L, 0L, 1
        );

        CurrentBasalStatusResponse parsedRes = (CurrentBasalStatusResponse) MessageTester.test(
                "0003290309200300000000000001d8d0",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}