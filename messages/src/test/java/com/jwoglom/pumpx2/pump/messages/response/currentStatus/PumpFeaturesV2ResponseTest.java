package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class PumpFeaturesV2ResponseTest {
    // APIv2.5
    @Test
    public void testPumpSupportedFeaturesResponseIndex0MainFeatures() throws DecoderException {
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
            // int status, int supportedFeaturesIndex, long pumpFeaturesBitmask
            0, 0, 1982127514L
        );

        // with input 0:
        // 2022-07-13 15:55:59.078 29750-29750/com.jwoglom.pumpx2.example I/X2:BluetoothHandler: Parsed response for 0006a1060600009add24767397: Optional.of(PumpFeaturesV2Response[
        // pumpFeaturesBitmask=1982127514,status=0,supportedFeatureIndex=0,cargo={0,0,-102,-35,36,118}])

        PumpFeaturesV2Response parsedRes = (PumpFeaturesV2Response) MessageTester.test(
                "0006a1060600009add24767397",
                6,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // APIv2.5
    @Test
    public void testPumpSupportedFeaturesResponseIndex1CIQProFeatures() throws DecoderException {
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                // int status, int supportedFeaturesIndex, long pumpFeaturesBitmask
                1, 1, 0L
        );

        PumpFeaturesV2Response parsedRes = (PumpFeaturesV2Response) MessageTester.test(
                "0004a10406010100000000a6aa",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // APIv2.5
    @Test
    public void testPumpSupportedFeaturesResponseIndex2Unknown() throws DecoderException {
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                // int status, int supportedFeaturesIndex, long pumpFeaturesBitmask
                0, 2, 4L
        );

        PumpFeaturesV2Response parsedRes = (PumpFeaturesV2Response) MessageTester.test(
                "0004a1040600020400000025cb",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    // APIv2.5
    @Test
    public void testPumpSupportedFeaturesResponseIndex3Control() throws DecoderException {
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                // int status, int supportedFeaturesIndex, long pumpFeaturesBitmask
                1, 3, 0L
        );

        PumpFeaturesV2Response parsedRes = (PumpFeaturesV2Response) MessageTester.test(
                "0004a1040601030000000025ee",
                4,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}