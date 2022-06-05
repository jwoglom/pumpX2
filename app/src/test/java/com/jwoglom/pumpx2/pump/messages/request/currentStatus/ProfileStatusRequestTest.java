package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ProfileStatusRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class ProfileStatusRequestTest {
    @Test
    public void testProfileStatusRequest() throws DecoderException {
        // empty cargo
        ProfileStatusRequest expected = new ProfileStatusRequest();

        ProfileStatusRequest parsedReq = (ProfileStatusRequest) MessageTester.test(
                "00033e03006b47",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}