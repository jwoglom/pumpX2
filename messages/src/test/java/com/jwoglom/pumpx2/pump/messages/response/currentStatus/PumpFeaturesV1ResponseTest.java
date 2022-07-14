package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

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
                // 0000000000000000000000000000000000000010001000001101110110011010
                BigInteger.valueOf(35708314L));
                // UNKNOWN BITS:
                // 0000000000000000000000000000000000000000001000001101100110011000
                // 3, 4, 7, 8, 11, 12, 14, 15, 21

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
            PumpFeaturesV1Response.PumpFeatureType.CONTROL_IQ_SUPPORTED,
            PumpFeaturesV1Response.PumpFeatureType.AUTO_POP_SUPPORTED
        ), parsedRes.getPrimaryFeatures());
    }

    @Test
    public void testPumpFeaturesResponsePumpSW74() throws DecoderException {
        PumpFeaturesV1Response expected = new PumpFeaturesV1Response(
                // 0000000000000000000000000000000000100110001001001101110110011010
                BigInteger.valueOf(639950234L));
                // UNKNOWN BITS:
                // 0000000000000000000000000000000000000100001000001101100110011000
                // 3, 4, 7, 8, 11, 12, 14, 15, 21, 26

        PumpFeaturesV1Response parsedRes = (PumpFeaturesV1Response) MessageTester.test(
                "00044f04089add2426000000009693",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        // The only parsed values.
        assertEquals(ImmutableSet.of(
                PumpFeaturesV1Response.PumpFeatureType.DEXCOM_G6_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.CONTROL_IQ_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.BASAL_LIMIT_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.AUTO_POP_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.PUMP_SETTINGS_IN_IDP_GUI_SUPPORTED
        ), parsedRes.getPrimaryFeatures());
    }

    @Test
    public void testPumpFeaturesResponsePumpSW76() throws DecoderException {
        PumpFeaturesV1Response expected = new PumpFeaturesV1Response(
                // 0000000000000000000000000000000001110110001001001101110110011010
                BigInteger.valueOf(1982127514L));
                // UNKNOWN BITS:
                // 0000000000000000000000000000000001000100001000001101100110011000
                // 3, 4, 7, 8, 11, 12, 14, 15, 21, 26, 30

        PumpFeaturesV1Response parsedRes = (PumpFeaturesV1Response) MessageTester.test(
                "00034f03089add2476000000005e9a",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        // The only parsed values.
        assertEquals(ImmutableSet.of(
                PumpFeaturesV1Response.PumpFeatureType.DEXCOM_G6_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.CONTROL_IQ_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.BASAL_LIMIT_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.AUTO_POP_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.BLE_PUMP_CONTROL_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.PUMP_SETTINGS_IN_IDP_GUI_SUPPORTED
        ), parsedRes.getPrimaryFeatures());
    }
}
