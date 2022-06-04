package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpSettingsRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpSettingsRequestTest {
    @Test
    public void testPumpSettingsRequest() throws DecoderException {
        // empty cargo
        PumpSettingsRequest expected = new PumpSettingsRequest();

        PumpSettingsRequest parsedReq = (PumpSettingsRequest) MessageTester.test(
                "0004520400f630",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}