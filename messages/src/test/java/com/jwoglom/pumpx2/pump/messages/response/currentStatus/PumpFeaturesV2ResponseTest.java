package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.Set;

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
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.MAIN_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(Set.of(
                PumpFeaturesV1Response.PumpFeatureType.DEXCOM_G6_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.CONTROL_IQ_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.BASAL_LIMIT_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.AUTO_POP_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.BLE_PUMP_CONTROL_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.PUMP_SETTINGS_IN_IDP_GUI_SUPPORTED
        ), parsedRes.getPrimaryFeatures());
    }

    // APIv2.5
    @Test
    public void testPumpSupportedFeaturesResponseIndex1ControlIqProFeatures() throws DecoderException {
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
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.CONTROL_IQ_PRO_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(Set.of(), parsedRes.getControlIqProFeatures());
    }

    // APIv2.5
    @Test
    public void testPumpSupportedFeaturesResponseIndex2ControlFeatures() throws DecoderException {
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
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.CONTROL_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(Set.of(
                PumpFeaturesV2Response.ControlFeatureType.STANDARD_BOLUS_CONTROL
        ), parsedRes.getControlFeatures());
    }

    // APIv2.5
    @Test
    public void testPumpSupportedFeaturesResponseIndex3ControlIqFeatures() throws DecoderException {
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
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.CONTROL_IQ_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(Set.of(), parsedRes.getControlIqFeatures());
    }

    @Test
    public void testPumpSupportedFeaturesResponseIndex4DexcomFeatures() throws DecoderException {
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                // int status, int supportedFeaturesIndex, long pumpFeaturesBitmask
                0, 4, 26L
        );

        PumpFeaturesV2Response parsedRes = (PumpFeaturesV2Response) MessageTester.test(
                "0006a1060600041a000000fb30",
                6,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.DEXCOM_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(Set.of(
                PumpFeaturesV2Response.DexcomFeatureType.G6_CGM,
                PumpFeaturesV2Response.DexcomFeatureType.AUTO_CAL,
                PumpFeaturesV2Response.DexcomFeatureType.DISABLE_GRAPH_SMOOTHING
        ), parsedRes.getDexcomFeatures());
    }

    // Mobi v3.5
    @Test
    public void testPumpSupportedFeaturesControlBitmaskDecoding() {
        PumpFeaturesV2Response parsedRes = new PumpFeaturesV2Response(
                // int status, int supportedFeaturesIndex, long pumpFeaturesBitmask
                0, 2, 249425407L
        );

        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.CONTROL_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(Set.of(
                PumpFeaturesV2Response.ControlFeatureType.CHANGE_CARTRIDGE_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.BASAL_DELIVERY_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.STANDARD_BOLUS_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.EXTENDED_BOLUS_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.TEMP_RATE_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.CGM_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.AAM_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.SHELF_MODE_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.IDP_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.UNREGISTER_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.PUMP_TIME_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.TZ_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.REMINDER_SETTINGS_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.ALERT_SETTINGS_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.ANNUM_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.PUMP_GLOBAL_SETTINGS_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.FACTORY_CONFIG_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.SNOOZE_SETTINGS_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.FIND_MY_PUMP_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.BOLUS_OVERFLOW_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.USER_INTERACTION_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.BLOOD_GLUCOSE_ENTRY_CONTROL,
                PumpFeaturesV2Response.ControlFeatureType.HLOG_CREATOR_CONTROL
        ), parsedRes.getControlFeatures());
    }

    @Test
    public void testPumpSupportedFeaturesAbbottAndSecondaryBitmaskDecoding() {
        PumpFeaturesV2Response abbott = new PumpFeaturesV2Response(0, 5, 1L);
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.ABBOTT_FEATURES, abbott.getSupportedFeatureIndex());
        assertEquals(Set.of(
                PumpFeaturesV2Response.AbbottFeatureType.IS_FSL2
        ), abbott.getAbbottFeatures());

        PumpFeaturesV2Response secondary = new PumpFeaturesV2Response(0, 6, 5L);
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.SECONDARY_FEATURES, secondary.getSupportedFeatureIndex());
        assertEquals(Set.of(
                PumpFeaturesV2Response.SecondaryFeatureType.BASELINE_PROFILES,
                PumpFeaturesV2Response.SecondaryFeatureType.BLE_TDU
        ), secondary.getSecondaryFeatures());
    }
}
