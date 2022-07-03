package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ExtendedBolusStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ExtendedBolusStatusResponseTest {
    @Test
    public void testExtendedBolusStatusResponseEmpty() throws DecoderException {
        ExtendedBolusStatusResponse expected = new ExtendedBolusStatusResponse(
            // int bolusStatus, int bolusId, long timestamp, long requestedVolume, long duration, int bolusSource
            0, 0, 0, 0, 0, 0
        );

        ExtendedBolusStatusResponse parsedRes = (ExtendedBolusStatusResponse) MessageTester.test(
                "00062f06120000000000000000000000000000000000007cdd",
                6,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}