package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMOORAlertSettingsRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class CGMOORAlertSettingsRequestTest {
    @Test
    public void testCGMOORAlertSettingsRequest() throws DecoderException {
        // empty cargo
        CGMOORAlertSettingsRequest expected = new CGMOORAlertSettingsRequest();

        CGMOORAlertSettingsRequest parsedReq = (CGMOORAlertSettingsRequest) MessageTester.test(
                "00035e030000dc",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}