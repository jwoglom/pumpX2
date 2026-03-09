package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.Set;

public class PumpFeaturesV2ResponseTest {
    private PumpFeaturesV2Response parseCargo(String cargoHex) throws DecoderException {
        PumpFeaturesV2Response parsedRes = new PumpFeaturesV2Response();
        parsedRes.parse(Hex.decodeHex(cargoHex));
        return parsedRes;
    }

    @Test
    public void testPumpSupportedFeaturesResponseIndex0MainFeatures() throws DecoderException {
        // cargoHex: 000012956576
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                0, 0, 1986368786L
        );
        PumpFeaturesV2Response parsedRes = parseCargo("000012956576");

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
        assertEquals(0, parsedRes.getSupportedFeatureIndexId());
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.MAIN_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(1986368786L, parsedRes.getPumpFeaturesBitmask());
        assertEquals(Set.of(
                PumpFeaturesV1Response.PumpFeatureType.DEXCOM_G6_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.CONTROL_IQ_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.WOMBAT_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.BASAL_LIMIT_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.AUTO_POP_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.BLE_PUMP_CONTROL_SUPPORTED,
                PumpFeaturesV1Response.PumpFeatureType.PUMP_SETTINGS_IN_IDP_GUI_SUPPORTED
        ), parsedRes.getPrimaryFeatures());
    }

    @Test
    public void testPumpSupportedFeaturesResponseIndex1ControlIqProFeatures() throws DecoderException {
        // cargoHex: 010100000000
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                1, 1, 0L
        );
        PumpFeaturesV2Response parsedRes = parseCargo("010100000000");

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(1, parsedRes.getStatus());
        assertEquals(1, parsedRes.getSupportedFeatureIndexId());
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.CONTROL_IQ_PRO_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(0L, parsedRes.getPumpFeaturesBitmask());
        assertEquals(Set.of(), parsedRes.getControlIqProFeatures());
    }

    @Test
    public void testPumpSupportedFeaturesResponseIndex2ControlFeatures() throws DecoderException {
        // cargoHex: 0002ffeddd0e
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                0, 2, 249425407L
        );
        PumpFeaturesV2Response parsedRes = parseCargo("0002ffeddd0e");

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
        assertEquals(2, parsedRes.getSupportedFeatureIndexId());
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.CONTROL_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(249425407L, parsedRes.getPumpFeaturesBitmask());
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
    public void testPumpSupportedFeaturesResponseIndex3ControlIqFeatures() throws DecoderException {
        // cargoHex: 000380030000
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                0, 3, 896L
        );
        PumpFeaturesV2Response parsedRes = parseCargo("000380030000");

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
        assertEquals(3, parsedRes.getSupportedFeatureIndexId());
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.CONTROL_IQ_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(896L, parsedRes.getPumpFeaturesBitmask());
        assertEquals(Set.of(
                PumpFeaturesV2Response.ControlIqFeatureType.CONTROL_IQ_VERSION_ONE_FIVE,
                PumpFeaturesV2Response.ControlIqFeatureType.TEMP_RATE_ENABLED
        ), parsedRes.getControlIqFeatures());
    }

    @Test
    public void testPumpSupportedFeaturesResponseIndex4DexcomFeatures() throws DecoderException {
        // cargoHex: 00047e000000
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                0, 4, 126L
        );
        PumpFeaturesV2Response parsedRes = parseCargo("00047e000000");

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
        assertEquals(4, parsedRes.getSupportedFeatureIndexId());
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.DEXCOM_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(126L, parsedRes.getPumpFeaturesBitmask());
        assertEquals(Set.of(
                PumpFeaturesV2Response.DexcomFeatureType.G6_CGM,
                PumpFeaturesV2Response.DexcomFeatureType.G7_CGM,
                PumpFeaturesV2Response.DexcomFeatureType.AUTO_CAL,
                PumpFeaturesV2Response.DexcomFeatureType.DISABLE_GRAPH_SMOOTHING
        ), parsedRes.getDexcomFeatures());
    }

    @Test
    public void testPumpSupportedFeaturesResponseIndex5AbbottFeatures() throws DecoderException {
        // cargoHex: 010500000000
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                1, 5, 0L
        );
        PumpFeaturesV2Response parsedRes = parseCargo("010500000000");

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(1, parsedRes.getStatus());
        assertEquals(5, parsedRes.getSupportedFeatureIndexId());
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.ABBOTT_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(0L, parsedRes.getPumpFeaturesBitmask());
        assertEquals(Set.of(), parsedRes.getAbbottFeatures());
    }

    @Test
    public void testPumpSupportedFeaturesResponseIndex6SecondaryFeatures() throws DecoderException {
        // cargoHex: 00062e000000
        PumpFeaturesV2Response expected = new PumpFeaturesV2Response(
                0, 6, 46L
        );
        PumpFeaturesV2Response parsedRes = parseCargo("00062e000000");

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(0, parsedRes.getStatus());
        assertEquals(6, parsedRes.getSupportedFeatureIndexId());
        assertEquals(PumpFeaturesV2Response.SupportedFeatureIndex.SECONDARY_FEATURES, parsedRes.getSupportedFeatureIndex());
        assertEquals(46L, parsedRes.getPumpFeaturesBitmask());
        assertEquals(Set.of(
                PumpFeaturesV2Response.SecondaryFeatureType.MULTI_CGM,
                PumpFeaturesV2Response.SecondaryFeatureType.BLE_TDU
        ), parsedRes.getSecondaryFeatures());
    }
}
