package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.LocalizationRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class LocalizationRequestTest {
    @Test
    public void testLocalizationRequest() throws DecoderException {
        // empty cargo
        LocalizationRequest expected = new LocalizationRequest();

        LocalizationRequest parsedReq = (LocalizationRequest) MessageTester.test(
                "0004a60400640f",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}