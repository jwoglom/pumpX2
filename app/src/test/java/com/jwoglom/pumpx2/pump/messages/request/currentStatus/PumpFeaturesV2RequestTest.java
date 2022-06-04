package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpFeaturesV2RequestTest {
    @Test
    public void testPumpSupportedFeaturesRequest() throws DecoderException {
        // empty cargo
        PumpFeaturesV2Request expected = new PumpFeaturesV2Request();

        PumpFeaturesV2Request parsedReq = (PumpFeaturesV2Request) MessageTester.test(
                "0003a003005324",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}