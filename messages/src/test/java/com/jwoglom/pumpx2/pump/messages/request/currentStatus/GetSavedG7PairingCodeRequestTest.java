package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class GetSavedG7PairingCodeRequestTest {
    @Test
    public void testSavedG7PairingCodeRequest() throws DecoderException {
        // empty cargo
        GetSavedG7PairingCodeRequest expected = new GetSavedG7PairingCodeRequest();

        GetSavedG7PairingCodeRequest parsedReq = (GetSavedG7PairingCodeRequest) MessageTester.test(
                "00247424007602",
                36,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}