package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class GetSavedG7PairingCodeResponseTest {
    @Test
    public void testSavedG7PairingCodeResponse_present() throws DecoderException {
        GetSavedG7PairingCodeResponse expected = new GetSavedG7PairingCodeResponse(
            5121
        );

        GetSavedG7PairingCodeResponse parsedRes = (GetSavedG7PairingCodeResponse) MessageTester.test(
                "0024752402011486dd",
                36,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}