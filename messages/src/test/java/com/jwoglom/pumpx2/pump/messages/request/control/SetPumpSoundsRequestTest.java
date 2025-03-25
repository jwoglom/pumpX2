package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.SetPumpSoundsRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetPumpSoundsRequestTest {
    @Test
    public void testSetPumpSoundsRequest_allBeep() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpSoundsRequest expected = new SetPumpSoundsRequest(
                new byte[]{0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        0,
                        2,
                        -74}
        );

        SetPumpSoundsRequest parsedReq = (SetPumpSoundsRequest) MessageTester.test(
                "0282e482210000000000000002b68abd68206095",
                -4,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0182305a4bf21a40e5554d391ed6f3eaa0d63126",
                "00829bd3"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }



    @Test
    public void testSetPumpSoundsRequest_allVibrate_snoozeOff() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpSoundsRequest expected = new SetPumpSoundsRequest(
                new byte[]{0,
                        3,
                        3,
                        3,
                        3,
                        3,
                        0,
                        0,
                        16}
        );

        SetPumpSoundsRequest parsedReq = (SetPumpSoundsRequest) MessageTester.test(
                "02fce4fc2100030303030300001018b66820aab8",
                -4,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01fc14e23ecd3308bb03f0af5a43ac1f63135fda",
                "00fc3076"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testSetPumpSoundsRequest_allVibrate_generalBeep() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpSoundsRequest expected = new SetPumpSoundsRequest(
                new byte[]{0,
                        3,
                        0,
                        3,
                        3,
                        3,
                        0,
                        0,
                        4}
        );

        SetPumpSoundsRequest parsedReq = (SetPumpSoundsRequest) MessageTester.test(
                "023ee43e2100030003030300000466b96820dcfb",
                62,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "013e51b3f7f5e423ff70485325af14cf9e1581e1",
                "003e2192"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testSetPumpSoundsRequest_allVibrate_quickbolusBeep() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpSoundsRequest expected = new SetPumpSoundsRequest(
                new byte[]{0,
                        0,
                        3,
                        3,
                        3,
                        3,
                        0,
                        0,
                        6}
        );

        SetPumpSoundsRequest parsedReq = (SetPumpSoundsRequest) MessageTester.test(
                "0241e4412100000303030300000678b9682013b8",
                65,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01416774d031c6e0e61484fb9afe3f6383ad2dc2",
                "004181de"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }



    @Test
    public void testSetPumpSoundsRequest_allVibrate_remindersBeep() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpSoundsRequest expected = new SetPumpSoundsRequest(
                new byte[]{0,
                        3,
                        3,
                        0,
                        3,
                        3,
                        0,
                        0,
                        8}
        );

        SetPumpSoundsRequest parsedReq = (SetPumpSoundsRequest) MessageTester.test(
                "026be46b21000303000303000008f1b9682085de",
                107,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "016bfbade6e070ae43679f929b46a1107db5f2e9",
                "006bd07d"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }



    @Test
    public void testSetPumpSoundsRequest_allVibrate_alarmBeep() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpSoundsRequest expected = new SetPumpSoundsRequest(
                new byte[]{0,
                        3,
                        3,
                        3,
                        3,
                        0,
                        0,
                        0,
                        34}
        );

        SetPumpSoundsRequest parsedReq = (SetPumpSoundsRequest) MessageTester.test(
                "0244e444210003030303000000228db96820c33d",
                68,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01448a8d342669baf97ac2f7f83e059482139e68",
                "0044928b"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }



    @Test
    public void testSetPumpSoundsRequest_allVibrate_alertBeep() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpSoundsRequest expected = new SetPumpSoundsRequest(
                new byte[]{0,
                        3,
                        3,
                        3,
                        0,
                        3,
                        0,
                        0,
                        48}
        );

        SetPumpSoundsRequest parsedReq = (SetPumpSoundsRequest) MessageTester.test(
                "024ee44e21000303030003000030a5b968201053",
                78,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "014ed8b377b6d8587d9c4e78b31fc2c309586fa6",
                "004e7101"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testSetPumpSoundsRequest_allVibrate_cgmAlertBeep() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpSoundsRequest expected = new SetPumpSoundsRequest(
                new byte[]{0,
                        3,
                        3,
                        3,
                        3,
                        3,
                        0,
                        2,
                        -112}
        );

        SetPumpSoundsRequest parsedReq = (SetPumpSoundsRequest) MessageTester.test(
                "0251e45121000303030303000290b7b96820bd57",
                81,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0151cdff118cfdbb501489e5794509c4e23d722c",
                "0051c6af"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testSetPumpSoundsRequest_allVibrate_cgmAlertHypoRepeat() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpSoundsRequest expected = new SetPumpSoundsRequest(
                new byte[]{0,
                        3,
                        3,
                        3,
                        3,
                        3,
                        0,
                        3,
                        -128}
        );

        SetPumpSoundsRequest parsedReq = (SetPumpSoundsRequest) MessageTester.test(
                "0254e45421000303030303000380c9b96820ca71",
                84,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0154f054052110f1cf33eb862cadeaac9f0dc1a6",
                "0054b049"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testSetPumpSoundsRequest_allVibrate_cgmAlertVibrate() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetPumpSoundsRequest expected = new SetPumpSoundsRequest(
                new byte[]{0,
                        3,
                        3,
                        3,
                        3,
                        3,
                        0,
                        0,
                        -128}
        );

        SetPumpSoundsRequest parsedReq = (SetPumpSoundsRequest) MessageTester.test(
                "0260e46021000303030303000080dab96820345f",
                96,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0160a95aac1b491ac7317041b822781728cbfa90",
                "00601f19"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}