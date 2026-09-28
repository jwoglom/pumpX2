package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetQuickBolusSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.SetQuickBolusSettingsRequest.ChangedField;
import com.jwoglom.pumpx2.pump.messages.request.control.SetQuickBolusSettingsRequest.QuickBolusIncrement;
import com.jwoglom.pumpx2.pump.messages.request.control.SetQuickBolusSettingsRequest.QuickBolusMode;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpGlobalsResponse;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.EnumSet;
import java.util.Set;

/**
 * Expected cargos are the Tandem Mobi app's own writes. Each carries the pump's current value in
 * every field not named in changedFields.
 */
public class SetQuickBolusSettingsRequestTest {

    private static void assertFields(SetQuickBolusSettingsRequest req, boolean enabled, QuickBolusMode mode, int incrementUnits, int incrementCarbs, Set<ChangedField> changedFields) {
        assertEquals("enabled", enabled, req.isEnabled());
        assertEquals("mode", mode, req.getMode());
        assertEquals("incrementUnits", incrementUnits, req.getIncrementUnits());
        assertEquals("incrementCarbs", incrementCarbs, req.getIncrementCarbs());
        assertEquals("changedFields", changedFields, req.getChangedFields());
    }

    private static void assertCargo(String hex, boolean enabled, QuickBolusMode mode, int incrementUnits, int incrementCarbs, Set<ChangedField> changedFields) throws DecoderException {
        SetQuickBolusSettingsRequest parsed = new SetQuickBolusSettingsRequest(Hex.decodeHex(hex));
        assertFields(parsed, enabled, mode, incrementUnits, incrementCarbs, changedFields);

        SetQuickBolusSettingsRequest built = new SetQuickBolusSettingsRequest(enabled, mode.getRaw(), incrementUnits, incrementCarbs, ChangedField.toBitmask(changedFields));
        assertEquals("built cargo", hex, Hex.encodeHexString(built.getCargo()));
    }

    private static PumpGlobalsResponse globals(boolean enabled, QuickBolusMode mode, int incrementUnits, int incrementCarbs) {
        return new PumpGlobalsResponse(enabled ? 1 : 0, incrementUnits, incrementCarbs, mode.getRaw(), 1, 0, 0, 0, 0, 0, 0, 0);
    }

    private static void assertChange(String hex, PumpGlobalsResponse current, boolean enabled, QuickBolusMode mode, int increment) {
        assertEquals(hex, Hex.encodeHexString(SetQuickBolusSettingsRequest.forChange(current, enabled, mode, increment).getCargo()));
    }


    @Test
    public void testSetQuickBolusSettingsRequest_disableCarryingHalfUnit() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetQuickBolusSettingsRequest expected = new SetQuickBolusSettingsRequest(
                new byte[]{0,0,-12,1,-48,7,1}
        );

        SetQuickBolusSettingsRequest parsedReq = (SetQuickBolusSettingsRequest) MessageTester.test(
                "0127d2271f0000f401d0070192b3f01f4553dc52",
                39,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0027653feef6a3f69bbed8a924ba965729772f31"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertFields(parsedReq, false, QuickBolusMode.UNITS, 500, 2000, EnumSet.of(ChangedField.ENABLED));
        assertEquals(QuickBolusIncrement.DISABLED, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_enableCarryingHalfUnit() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetQuickBolusSettingsRequest expected = new SetQuickBolusSettingsRequest(
                new byte[]{1,0,-12,1,-48,7,1}
        );

        SetQuickBolusSettingsRequest parsedReq = (SetQuickBolusSettingsRequest) MessageTester.test(
                "012fd22f1f0100f401d0070199b3f01f7b890678",
                47,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "002fc35d3e9c65a3b5bde600a7a1aab82334acba"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertFields(parsedReq, true, QuickBolusMode.UNITS, 500, 2000, EnumSet.of(ChangedField.ENABLED));
        assertEquals(QuickBolusIncrement.UNITS_0_5, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_incrementOneUnit() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetQuickBolusSettingsRequest expected = new SetQuickBolusSettingsRequest(
                new byte[]{1,0,-24,3,-48,7,4}
        );

        SetQuickBolusSettingsRequest parsedReq = (SetQuickBolusSettingsRequest) MessageTester.test(
                "0143d2431f0100e803d0070444b5f01ff65606f6",
                67,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0043a06ada7fe304173af8dec2e3741e2fa6b07a"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertFields(parsedReq, true, QuickBolusMode.UNITS, 1000, 2000, EnumSet.of(ChangedField.INCREMENT_UNITS));
        assertEquals(QuickBolusIncrement.UNITS_1_0, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_incrementTwoUnits() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetQuickBolusSettingsRequest expected = new SetQuickBolusSettingsRequest(
                new byte[]{1,0,-48,7,-48,7,4}
        );

        SetQuickBolusSettingsRequest parsedReq = (SetQuickBolusSettingsRequest) MessageTester.test(
                "0145d2451f0100d007d007045fb5f01fdf82adc2",
                69,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00458f3f2a95494ed7fe9486d8c010f179e7f356"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertFields(parsedReq, true, QuickBolusMode.UNITS, 2000, 2000, EnumSet.of(ChangedField.INCREMENT_UNITS));
        assertEquals(QuickBolusIncrement.UNITS_2_0, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_incrementFiveUnits() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetQuickBolusSettingsRequest expected = new SetQuickBolusSettingsRequest(
                new byte[]{1,0,-120,19,-48,7,4}
        );

        SetQuickBolusSettingsRequest parsedReq = (SetQuickBolusSettingsRequest) MessageTester.test(
                "014ed24e1f01008813d007047db5f01f0925f3b8",
                78,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "004e7e7ff27351e18c8547c8a0f9aa1f5c7d39ad"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertFields(parsedReq, true, QuickBolusMode.UNITS, 5000, 2000, EnumSet.of(ChangedField.INCREMENT_UNITS));
        assertEquals(QuickBolusIncrement.UNITS_5_0, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_incrementTwoGrams() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetQuickBolusSettingsRequest expected = new SetQuickBolusSettingsRequest(
                new byte[]{1,1,-120,19,-48,7,8}
        );

        SetQuickBolusSettingsRequest parsedReq = (SetQuickBolusSettingsRequest) MessageTester.test(
                "01b2d2b21f01018813d0070851b7f01ff804b107",
                -78,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00b2a7cb9e77051542d5c9d8b5253269836e201a"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertFields(parsedReq, true, QuickBolusMode.CARBS, 5000, 2000, EnumSet.of(ChangedField.INCREMENT_CARBS));
        assertEquals(QuickBolusIncrement.CARBS_2G, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_incrementFiveGrams() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetQuickBolusSettingsRequest expected = new SetQuickBolusSettingsRequest(
                new byte[]{1,1,-120,19,-120,19,8}
        );

        SetQuickBolusSettingsRequest parsedReq = (SetQuickBolusSettingsRequest) MessageTester.test(
                "0159d2591f01018813881308bdb5f01f9cd3f104",
                89,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "005905fe4933df7b80cada7ae5e4f8f907056791"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertFields(parsedReq, true, QuickBolusMode.CARBS, 5000, 5000, EnumSet.of(ChangedField.INCREMENT_CARBS));
        assertEquals(QuickBolusIncrement.CARBS_5G, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_incrementTenGrams() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetQuickBolusSettingsRequest expected = new SetQuickBolusSettingsRequest(
                new byte[]{1,1,-120,19,16,39,8}
        );

        SetQuickBolusSettingsRequest parsedReq = (SetQuickBolusSettingsRequest) MessageTester.test(
                "015bd25b1f01018813102708d8b5f01f6f9db561",
                91,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "005b23cd6e7900a0ebf7c9d4d626af1d33b5aa93"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertFields(parsedReq, true, QuickBolusMode.CARBS, 5000, 10000, EnumSet.of(ChangedField.INCREMENT_CARBS));
        assertEquals(QuickBolusIncrement.CARBS_10G, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_incrementFifteenGrams() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1L);

        SetQuickBolusSettingsRequest expected = new SetQuickBolusSettingsRequest(
                new byte[]{1,1,-120,19,-104,58,8}
        );

        SetQuickBolusSettingsRequest parsedReq = (SetQuickBolusSettingsRequest) MessageTester.test(
                "0164d2641f01018813983a08efb5f01f09fc0867",
                100,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "006415ef922302f56194ccaf44fffd72e3276df0"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertFields(parsedReq, true, QuickBolusMode.CARBS, 5000, 15000, EnumSet.of(ChangedField.INCREMENT_CARBS));
        assertEquals(QuickBolusIncrement.CARBS_15G, parsedReq.getIncrement());
    }

    @Test
    public void testCargo_disableCarryingOneUnit() throws DecoderException {
        assertCargo("0000e803d00701", false, QuickBolusMode.UNITS, 1000, 2000, EnumSet.of(ChangedField.ENABLED));
    }

    @Test
    public void testCargo_disableCarryingFiveUnits() throws DecoderException {
        assertCargo("00008813d00701", false, QuickBolusMode.UNITS, 5000, 2000, EnumSet.of(ChangedField.ENABLED));
    }

    @Test
    public void testCargo_enableCarryingOneUnit() throws DecoderException {
        assertCargo("0100e803d00701", true, QuickBolusMode.UNITS, 1000, 2000, EnumSet.of(ChangedField.ENABLED));
    }

    @Test
    public void testCargo_enableAtOneUnit() throws DecoderException {
        assertCargo("0100e803d00705", true, QuickBolusMode.UNITS, 1000, 2000, EnumSet.of(ChangedField.ENABLED, ChangedField.INCREMENT_UNITS));
    }

    @Test
    public void testCargo_incrementHalfUnit() throws DecoderException {
        assertCargo("0100f401d00704", true, QuickBolusMode.UNITS, 500, 2000, EnumSet.of(ChangedField.INCREMENT_UNITS));
    }

    @Test
    public void testCargo_switchToCarbs() throws DecoderException {
        assertCargo("01018813d00702", true, QuickBolusMode.CARBS, 5000, 2000, EnumSet.of(ChangedField.MODE));
    }

    @Test
    public void testForChange_enablingAtTheCurrentHalfUnit() {
        assertChange("0100f401d00701", globals(false, QuickBolusMode.UNITS, 500, 2000), true, QuickBolusMode.UNITS, 500);
    }

    @Test
    public void testForChange_enablingAtANewIncrementSendsBothFields() {
        assertChange("0100e803d00705", globals(false, QuickBolusMode.UNITS, 500, 2000), true, QuickBolusMode.UNITS, 1000);
    }

    @Test
    public void testForChange_changingToHalfUnit() {
        assertChange("0100f401d00704", globals(true, QuickBolusMode.UNITS, 1000, 2000), true, QuickBolusMode.UNITS, 500);
    }

    @Test
    public void testForChange_changingToFiveUnits() {
        assertChange("01008813d00704", globals(true, QuickBolusMode.UNITS, 2000, 2000), true, QuickBolusMode.UNITS, 5000);
    }

    @Test
    public void testForChange_disablingIgnoresModeAndIncrement() {
        assertChange("00008813d00701", globals(true, QuickBolusMode.UNITS, 5000, 2000), false, QuickBolusMode.CARBS, 0);
    }

    @Test
    public void testForChange_disablingInCarbsModeCarriesCarbsMode() {
        assertChange("00018813102701", globals(true, QuickBolusMode.CARBS, 5000, 10000), false, QuickBolusMode.UNITS, 0);
    }

    @Test
    public void testForChange_switchingToCarbsAtTheCurrentIncrement() {
        assertChange("01018813d00702", globals(true, QuickBolusMode.UNITS, 5000, 2000), true, QuickBolusMode.CARBS, 2000);
    }

    @Test
    public void testForChange_changingToFiveGrams() {
        assertChange("01018813881308", globals(true, QuickBolusMode.CARBS, 5000, 2000), true, QuickBolusMode.CARBS, 5000);
    }

    @Test
    public void testForChange_switchingBackToUnits() {
        assertChange("01008813102702", globals(true, QuickBolusMode.CARBS, 5000, 10000), true, QuickBolusMode.UNITS, 5000);
    }

    @Test
    public void testForChange_nothingChangesWhenThePumpAlreadyMatches() {
        assertEquals(0, SetQuickBolusSettingsRequest.forChange(globals(true, QuickBolusMode.UNITS, 2000, 2000), true, QuickBolusMode.UNITS, 2000).getChangedFieldsRaw());
    }

    @Test
    public void testForChange_unsupportedIncrementIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> SetQuickBolusSettingsRequest.forChange(globals(true, QuickBolusMode.UNITS, 500, 2000), true, QuickBolusMode.UNITS, 750));
        assertThrows(IllegalArgumentException.class, () -> SetQuickBolusSettingsRequest.forChange(globals(true, QuickBolusMode.CARBS, 500, 2000), true, QuickBolusMode.CARBS, 500));
    }

    @Test
    public void testSetQuickBolusSettingsRequest_singleArgConstructor() throws DecoderException {
        SetQuickBolusSettingsRequest[][] testCases = new SetQuickBolusSettingsRequest[][]{
            {
                new SetQuickBolusSettingsRequest(QuickBolusIncrement.DISABLED),
                new SetQuickBolusSettingsRequest(new byte[]{0,0,-12,1,-48,7,1})
            },
            {
                new SetQuickBolusSettingsRequest(QuickBolusIncrement.UNITS_0_5),
                new SetQuickBolusSettingsRequest(new byte[]{1,0,-12,1,-48,7,1})
            },
            {
                new SetQuickBolusSettingsRequest(QuickBolusIncrement.UNITS_1_0),
                new SetQuickBolusSettingsRequest(new byte[]{1,0,-24,3,-48,7,4})
            },
            {
                new SetQuickBolusSettingsRequest(QuickBolusIncrement.UNITS_2_0),
                new SetQuickBolusSettingsRequest(new byte[]{1,0,-48,7,-48,7,4})
            },
            {
                new SetQuickBolusSettingsRequest(QuickBolusIncrement.UNITS_5_0),
                new SetQuickBolusSettingsRequest(new byte[]{1,0,-120,19,-48,7,4})
            },
            {
                new SetQuickBolusSettingsRequest(QuickBolusIncrement.CARBS_2G),
                new SetQuickBolusSettingsRequest(new byte[]{1,1,-120,19,-48,7,8})
            },
            {
                new SetQuickBolusSettingsRequest(QuickBolusIncrement.CARBS_5G),
                new SetQuickBolusSettingsRequest(new byte[]{1,1,-120,19,-120,19,8})
            },
            {
                new SetQuickBolusSettingsRequest(QuickBolusIncrement.CARBS_10G),
                new SetQuickBolusSettingsRequest(new byte[]{1,1,-120,19,16,39,8})
            },
            {
                new SetQuickBolusSettingsRequest(QuickBolusIncrement.CARBS_15G),
                new SetQuickBolusSettingsRequest(new byte[]{1,1,-120,19,-104,58,8})
            },
        };

        int n = 0;
        for (SetQuickBolusSettingsRequest[] testCase : testCases) {
            System.out.println("TestCase#" + n + "[" + testCase[0] + " --- " + testCase[1] + "]");
            assertEquals("TestCase#" + n, testCase[0], testCase[1]);
            n++;
        }
    }

    @Test
    public void testThreeArgConstructorMatchesSingleArg() {
        for (QuickBolusIncrement i : QuickBolusIncrement.values()) {
            assertEquals(i.name(), new SetQuickBolusSettingsRequest(i), new SetQuickBolusSettingsRequest(i.isEnabled(), i.getMode(), i));
        }
    }

    @Test
    public void testThreeArgConstructorRejectsContradictoryEnabled() {
        assertThrows(IllegalArgumentException.class, () -> new SetQuickBolusSettingsRequest(true, QuickBolusMode.UNITS, QuickBolusIncrement.DISABLED));
        assertThrows(IllegalArgumentException.class, () -> new SetQuickBolusSettingsRequest(false, QuickBolusMode.UNITS, QuickBolusIncrement.UNITS_1_0));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testMagicIsTheCargoTail() {
        for (QuickBolusIncrement i : QuickBolusIncrement.values()) {
            assertHexEquals(i.getMagic(), new SetQuickBolusSettingsRequest(i).getMagic());
        }
    }

    @Test
    public void testGetIncrementFollowsTheFieldsNotTheCapture() throws DecoderException {
        assertEquals(QuickBolusIncrement.UNITS_1_0, new SetQuickBolusSettingsRequest(Hex.decodeHex("0100e803d00701")).getIncrement());
        assertEquals(QuickBolusIncrement.CARBS_2G, new SetQuickBolusSettingsRequest(Hex.decodeHex("01018813d00702")).getIncrement());
        assertEquals(QuickBolusIncrement.DISABLED, new SetQuickBolusSettingsRequest(Hex.decodeHex("00018813102701")).getIncrement());
        assertNull(new SetQuickBolusSettingsRequest(Hex.decodeHex("0100ee02d00704")).getIncrement());
    }

    @Test
    public void testParsesADisableInCarbsModeCarryingTheHalfUnitCapture() throws DecoderException {
        assertCargo("0001f401d00701", false, QuickBolusMode.CARBS, 500, 2000, EnumSet.of(ChangedField.ENABLED));
    }

    @Test
    public void testForChange_toIncrement() {
        assertChange("0100e803d00705", globals(false, QuickBolusMode.UNITS, 500, 2000), QuickBolusIncrement.UNITS_1_0);
        assertChange("0101881388130a", globals(true, QuickBolusMode.UNITS, 5000, 2000), QuickBolusIncrement.CARBS_5G);
        assertChange("00008813d00701", globals(true, QuickBolusMode.UNITS, 5000, 2000), QuickBolusIncrement.DISABLED);
    }

    private static void assertChange(String hex, PumpGlobalsResponse current, QuickBolusIncrement increment) {
        assertEquals(increment.name(), hex, Hex.encodeHexString(SetQuickBolusSettingsRequest.forChange(current, increment).getCargo()));
    }

    @Test
    public void testPumpGlobalsIncrement() {
        assertEquals(QuickBolusIncrement.DISABLED, globals(false, QuickBolusMode.CARBS, 1000, 5000).getQuickBolusIncrement());
        assertEquals(QuickBolusIncrement.UNITS_1_0, globals(true, QuickBolusMode.UNITS, 1000, 5000).getQuickBolusIncrement());
        assertEquals(QuickBolusIncrement.CARBS_5G, globals(true, QuickBolusMode.CARBS, 1000, 5000).getQuickBolusIncrement());
        assertNull(globals(true, QuickBolusMode.UNITS, 100, 1).getQuickBolusIncrement());
    }
}
