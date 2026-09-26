package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesV1Response.PumpFeatureType;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesV2Response.SupportedFeatureIndex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.EnumSet;

public class NewDayHistoryLogTest {
    @Test
    public void testNewDayHistoryLog1() throws DecoderException {
        NewDayHistoryLog expected = (NewDayHistoryLog) new NewDayHistoryLog(
            // long pumpTimeSec, long sequenceNum, float commandedBasalRate, long featuresBitmask, long featureBitmaskIndex
            580521600L, 480602L, 3.4590001F, 1986368786L, 93L
        ).withHeaderHighNibble(1);

        NewDayHistoryLog parsedRes = (NewDayHistoryLog) HistoryLogMessageTester.testSingle(
                "5a10800e9a225a55070042605d40129565765d00000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        // Tandem Mobi: the same main features and supported indices its PumpFeaturesV2Response reports.
        assertEquals(
                EnumSet.of(PumpFeatureType.DEXCOM_G6_SUPPORTED, PumpFeatureType.CONTROL_IQ_SUPPORTED,
                        PumpFeatureType.WOMBAT_SUPPORTED, PumpFeatureType.BASAL_LIMIT_SUPPORTED,
                        PumpFeatureType.AUTO_POP_SUPPORTED, PumpFeatureType.BLE_PUMP_CONTROL_SUPPORTED,
                        PumpFeatureType.PUMP_SETTINGS_IN_IDP_GUI_SUPPORTED),
                EnumSet.copyOf(parsedRes.getPrimaryFeatures()));
        assertEquals(
                EnumSet.of(SupportedFeatureIndex.MAIN_FEATURES, SupportedFeatureIndex.CONTROL_FEATURES,
                        SupportedFeatureIndex.CONTROL_IQ_FEATURES, SupportedFeatureIndex.DEXCOM_FEATURES,
                        SupportedFeatureIndex.SECONDARY_FEATURES),
                EnumSet.copyOf(parsedRes.getSupportedFeatureIndices()));
    }

    @Test
    public void testNewDayHistoryLog2() throws DecoderException {
        NewDayHistoryLog expected = (NewDayHistoryLog) new NewDayHistoryLog(
            // long pumpTimeSec, long sequenceNum, float commandedBasalRate, long featuresBitmask, long featureBitmaskIndex
            580694400L, 487757L, 0.0F, 1986368786L, 93L
        ).withHeaderHighNibble(1);

        NewDayHistoryLog parsedRes = (NewDayHistoryLog) HistoryLogMessageTester.testSingle(
                "5a1080b19c224d71070000000000129565765d00000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testNewDayHistoryLog_x2SupportedFeatureIndices() throws DecoderException {
        // Maintainer's t:slim X2 (2024, API 3.4). Header high nibble 0.
        NewDayHistoryLog expected = new NewDayHistoryLog(
            534384000L, 1060003L, 0.8F, 1982127514L, 125L
        );

        NewDayHistoryLog parsedRes = (NewDayHistoryLog) HistoryLogMessageTester.testSingle(
                "5a00800dda1fa32c1000cdcc4c3f9add24767d00000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(
                EnumSet.of(PumpFeatureType.DEXCOM_G6_SUPPORTED, PumpFeatureType.CONTROL_IQ_SUPPORTED,
                        PumpFeatureType.BASAL_LIMIT_SUPPORTED, PumpFeatureType.AUTO_POP_SUPPORTED,
                        PumpFeatureType.BLE_PUMP_CONTROL_SUPPORTED, PumpFeatureType.PUMP_SETTINGS_IN_IDP_GUI_SUPPORTED),
                EnumSet.copyOf(parsedRes.getPrimaryFeatures()));
        assertEquals(
                EnumSet.of(SupportedFeatureIndex.MAIN_FEATURES, SupportedFeatureIndex.CONTROL_FEATURES,
                        SupportedFeatureIndex.CONTROL_IQ_FEATURES, SupportedFeatureIndex.DEXCOM_FEATURES,
                        SupportedFeatureIndex.ABBOTT_FEATURES, SupportedFeatureIndex.SECONDARY_FEATURES),
                EnumSet.copyOf(parsedRes.getSupportedFeatureIndices()));
    }

    @Test
    public void testNewDayHistoryLog_olderX2FirmwareIndices() throws DecoderException {
        // Maintainer's t:slim X2 (2022, API 2.5), which does not support the Control-IQ index (3).
        NewDayHistoryLog expected = new NewDayHistoryLog(
            461462400L, 23508L, 0.0F, 1982127514L, 21L
        );

        NewDayHistoryLog parsedRes = (NewDayHistoryLog) HistoryLogMessageTester.testSingle(
                "5a00805b811bd45b0000000000009add24761500000000000000",
                expected
        );
        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
        assertEquals(
                EnumSet.of(SupportedFeatureIndex.MAIN_FEATURES, SupportedFeatureIndex.CONTROL_FEATURES,
                        SupportedFeatureIndex.DEXCOM_FEATURES),
                EnumSet.copyOf(parsedRes.getSupportedFeatureIndices()));
    }
}
