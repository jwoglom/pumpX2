package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.math.BigInteger;

public class PumpFeaturesV1ResponseTest {
    /**
     * The pump returns bits which aren't parsed by the app.
     */
    @Test
    public void testPumpFeaturesResponse() throws DecoderException {
        PumpFeaturesV1Response expected = new PumpFeaturesV1Response(
                // 0b10001000001101110110011010
                BigInteger.valueOf(35708314L));

        PumpFeaturesV1Response parsedRes = (PumpFeaturesV1Response) MessageTester.test(
                "00034f03089add2002000000007f08",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        // The only parsed values.
        assertEquals(ImmutableSet.of(
            PumpFeaturesV1Response.PumpFeatureType.DEXCOM_G6_SUPPORTED,
            PumpFeaturesV1Response.PumpFeatureType.AUTO_POP_SUPPORTED,
            PumpFeaturesV1Response.PumpFeatureType.CONTROL_IQ_SUPPORTED
        ), parsedRes.getFeatures());
    }
}
