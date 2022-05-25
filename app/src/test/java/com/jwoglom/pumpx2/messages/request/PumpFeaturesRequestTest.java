package com.jwoglom.pumpx2.messages.request;

import static com.jwoglom.pumpx2.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.request.PumpFeaturesRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpFeaturesRequestTest {
    @Test
    public void testPumpFeaturesRequest() throws DecoderException {
        // empty cargo
        PumpFeaturesRequest expected = new PumpFeaturesRequest();

        PumpFeaturesRequest parsedReq = (PumpFeaturesRequest) MessageTester.test(
                "00034e0300639f",
                2,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
