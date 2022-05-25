package com.jwoglom.pumpx2.pump.messages.response;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.math.BigInteger;

public class PumpFeaturesResponseTest {
    /**
     * The pump returns bits which aren't parsed by the app.
     */
    @Test
    public void testPumpFeaturesResponse() throws DecoderException {
        PumpFeaturesResponse expected = new PumpFeaturesResponse(
                // 0b10001000001101110110011010
                BigInteger.valueOf(35708314L));

        PumpFeaturesResponse parsedRes = (PumpFeaturesResponse) MessageTester.test(
                "00034f03089add2002000000007f08",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        // The only parsed values.
        assertEquals(ImmutableSet.of(
            PumpFeaturesResponse.PumpFeatureType.DEXCOM_G6_SUPPORTED,
            PumpFeaturesResponse.PumpFeatureType.AUTO_POP_SUPPORTED,
            PumpFeaturesResponse.PumpFeatureType.CONTROL_IQ_SUPPORTED
        ), parsedRes.getFeatures());
    }
}
