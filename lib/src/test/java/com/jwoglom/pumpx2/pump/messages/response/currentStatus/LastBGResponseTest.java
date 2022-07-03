package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.LastBGResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class LastBGResponseTest {
    @Test
    public void testLastBGResponseEmpty() throws DecoderException {
        LastBGResponse expected = new LastBGResponse(
            // long bgTimestamp, int bgValue, int bgSource
            0L, 0, 0
        );

        LastBGResponse parsedRes = (LastBGResponse) MessageTester.test(
                "0003330307000000000000003117",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}