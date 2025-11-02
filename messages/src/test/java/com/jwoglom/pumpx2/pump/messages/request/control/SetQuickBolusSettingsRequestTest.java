package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetQuickBolusSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.SetQuickBolusSettingsRequest.QuickBolusIncrement;
import com.jwoglom.pumpx2.pump.messages.request.control.SetQuickBolusSettingsRequest.QuickBolusMode;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetQuickBolusSettingsRequestTest {

    @Test
    public void testSetQuickBolusSettingsRequest_off() throws DecoderException {
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
        assertFalse(parsedReq.isEnabled());
        assertEquals(QuickBolusIncrement.DISABLED, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_on_05u() throws DecoderException {
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
        assertTrue(parsedReq.isEnabled());
        assertEquals(QuickBolusMode.UNITS, parsedReq.getMode());
        assertEquals(QuickBolusIncrement.UNITS_0_5, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_on_1u() throws DecoderException {
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
        assertTrue(parsedReq.isEnabled());
        assertEquals(QuickBolusMode.UNITS, parsedReq.getMode());
        assertEquals(QuickBolusIncrement.UNITS_1_0, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_on_2u() throws DecoderException {
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
        assertTrue(parsedReq.isEnabled());
        assertEquals(QuickBolusMode.UNITS, parsedReq.getMode());
        assertEquals(QuickBolusIncrement.UNITS_2_0, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_on_5u() throws DecoderException {
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
        assertTrue(parsedReq.isEnabled());
        assertEquals(QuickBolusMode.UNITS, parsedReq.getMode());
        assertEquals(QuickBolusIncrement.UNITS_5_0, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_on_carbs_2g() throws DecoderException {
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
        assertTrue(parsedReq.isEnabled());
        assertEquals(QuickBolusMode.CARBS, parsedReq.getMode());
        assertEquals(QuickBolusIncrement.CARBS_2G, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_on_carbs_5g() throws DecoderException {
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
        assertTrue(parsedReq.isEnabled());
        assertEquals(QuickBolusMode.CARBS, parsedReq.getMode());
        assertEquals(QuickBolusIncrement.CARBS_5G, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_on_carbs_10g() throws DecoderException {
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
        assertTrue(parsedReq.isEnabled());
        assertEquals(QuickBolusMode.CARBS, parsedReq.getMode());
        assertEquals(QuickBolusIncrement.CARBS_10G, parsedReq.getIncrement());
    }

    @Test
    public void testSetQuickBolusSettingsRequest_on_carbs_15g() throws DecoderException {
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
        assertTrue(parsedReq.isEnabled());
        assertEquals(QuickBolusMode.CARBS, parsedReq.getMode());
        assertEquals(QuickBolusIncrement.CARBS_15G, parsedReq.getIncrement());
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

}