package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class BasalLimitSettingsRequestTest {
    @Test
    public void testBasalLimitSettingsRequest() throws DecoderException {
        // empty cargo
        BasalLimitSettingsRequest expected = new BasalLimitSettingsRequest();

        BasalLimitSettingsRequest parsedReq = (BasalLimitSettingsRequest) MessageTester.test(
                "00048a0400c3fc",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}