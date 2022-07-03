package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpFeaturesV1RequestTest {
    @Test
    public void testPumpFeaturesRequest() throws DecoderException {
        // empty cargo
        PumpFeaturesV1Request expected = new PumpFeaturesV1Request();

        PumpFeaturesV1Request parsedReq = (PumpFeaturesV1Request) MessageTester.test(
                "00034e0300639f",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
