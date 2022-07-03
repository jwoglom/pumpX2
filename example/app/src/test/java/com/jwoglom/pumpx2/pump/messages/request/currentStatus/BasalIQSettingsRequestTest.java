package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.BasalIQSettingsRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BasalIQSettingsRequestTest {
    @Test
    public void testBasalIQSettingsRequest() throws DecoderException {
        // empty cargo
        BasalIQSettingsRequest expected = new BasalIQSettingsRequest();

        BasalIQSettingsRequest parsedReq = (BasalIQSettingsRequest) MessageTester.test(
                "000462040053f5",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}