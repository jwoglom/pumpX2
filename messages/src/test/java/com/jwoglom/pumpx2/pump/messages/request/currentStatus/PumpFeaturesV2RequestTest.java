package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpFeaturesV2RequestTest {
    @Test
    public void testPumpSupportedFeaturesRequestZero() throws DecoderException {
        //
        PumpFeaturesV2Request expected = new PumpFeaturesV2Request(0);

        PumpFeaturesV2Request parsedReq = (PumpFeaturesV2Request) MessageTester.test(
                "0006a006010027ef",
                6,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testPumpSupportedFeaturesRequestOne() throws DecoderException {
        //
        PumpFeaturesV2Request expected = new PumpFeaturesV2Request(1);

        PumpFeaturesV2Request parsedReq = (PumpFeaturesV2Request) MessageTester.test(
                "0004a00401016691",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}