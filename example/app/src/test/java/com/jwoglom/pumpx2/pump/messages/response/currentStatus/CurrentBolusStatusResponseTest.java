package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBolusStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CurrentBolusStatusResponseTest {
    @Test
    public void testCurrentBolusStatusResponseEmpty() throws DecoderException {
        CurrentBolusStatusResponse expected = new CurrentBolusStatusResponse(
            // int status, int bolusId, long timestamp, long requestedVolume, int bolusSource, int bolusTypeBitmask
                0, 0, 0L, 0, 0, 0
        );

        CurrentBolusStatusResponse parsedRes = (CurrentBolusStatusResponse) MessageTester.test(
                "00032d030f000000000000000000000000000000b109",
                3,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}