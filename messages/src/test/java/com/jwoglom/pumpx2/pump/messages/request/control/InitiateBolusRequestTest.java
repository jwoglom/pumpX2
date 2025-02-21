package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.BolusDeliveryHistoryLog;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.Set;

public class InitiateBolusRequestTest {
    @Test
    public void testInitiateBolusRequest_ID10650_1u() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1000735,timeSinceReset=461510642]
        // 1u override bolus
        // InitiateBolusRequest[bolusBG=0,bolusCarbs=0,bolusID=10650,bolusIOB=0.0,bolusTypeId=8,correctionVolume=0,empty1=0,empty2=0,empty3=0,foodVolume=0,totalVolume=1000,cargo={-24,3,0,0,-102,41,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-14,23,-126,27,104,-7,76,-21,-26,113,122,93,21,81,39,20,127,-20,-102,-39,121,-110,106,-84}]

        initPumpState("6VeDeRAL5DCigGw2", 461510642L);
        InitiateBolusRequest expected = new InitiateBolusRequest(
                stripHmacCargo(new byte[]{-24,3,0,0,-102,41,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-14,23,-126,27,104,-7,76,-21,-26,113,122,93,21,81,39,20,127,-20,-102,-39,121,-110,106,-84}));

        InitiateBolusRequest parsedReq = (InitiateBolusRequest) MessageTester.test(
                "03399e393de80300009a29000008000000000000",
                57,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0239000000000000000000000000000000000000",
                "013900000000f217821b68f94cebe6717a5d1551",
                "003927147fec9ad979926aaccd74"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(1000, parsedReq.getTotalVolume()); // 1.000u
        assertEquals(10650, parsedReq.getBolusID());
        assertEquals(8, parsedReq.getBolusTypeBitmask());
        assertEquals(Set.of(BolusDeliveryHistoryLog.BolusType.FOOD2), parsedReq.getBolusTypes());
        assertEquals(0, parsedReq.getFoodVolume()); // 0, since no carbs
        assertEquals(0, parsedReq.getCorrectionVolume());
        assertEquals(0, parsedReq.getBolusCarbs());
        assertEquals(0, parsedReq.getBolusBG()); // no BG entered
        assertEquals(0, parsedReq.getBolusIOB()); // no iob
    }

    @Test
    public void testInitiateBolusRequest_ID10652_013u_13g_carbs_142mgdl() throws DecoderException {
        // $ PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461589180
        // ./scripts/get-single-opcode.py '033e9e3e3d820000009c29000001820000000000 023e00000d008e00000000000000000000000000 013e00000000bc4a831b9cbf19ffb856288a8afa 003e8f24a463e00cf3bbe5d305dd'

        // TimeSinceResetResponse[pumpTime=1079274,timeSinceReset=461589180]
        initPumpState("6VeDeRAL5DCigGw2", 461589180L);
        InitiateBolusRequest expected = new InitiateBolusRequest(
                stripHmacCargo(new byte[]{-126, 0, 0, 0, -100, 41, 0, 0, 1, -126, 0, 0, 0, 0, 0, 0, 0, 13, 0, -114, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -68, 74, -125, 27, -100, -65, 25, -1, -72, 86, 40, -118, -118, -6, -113, 36, -92, 99, -32, 12, -13, -69, -27, -45}));

        InitiateBolusRequest parsedReq = (InitiateBolusRequest) MessageTester.test(
                "033e9e3e3d820000009c29000001820000000000",
                62,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "023e00000d008e00000000000000000000000000",
                "013e00000000bc4a831b9cbf19ffb856288a8afa",
                "003e8f24a463e00cf3bbe5d305dd"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(130, parsedReq.getTotalVolume()); // 0.13u
        assertEquals(10652, parsedReq.getBolusID());
        assertEquals(1, parsedReq.getBolusTypeBitmask());
        assertEquals(Set.of(BolusDeliveryHistoryLog.BolusType.FOOD1), parsedReq.getBolusTypes());
        assertEquals(130, parsedReq.getFoodVolume()); // 0.11u
        assertEquals(0, parsedReq.getCorrectionVolume());
        assertEquals(13, parsedReq.getBolusCarbs());
        assertEquals(142, parsedReq.getBolusBG());
        assertEquals(0, parsedReq.getBolusIOB());
    }

    @Test
    public void testInitiateBolusRequest_ID10653_011u_11g_carbs_161mgdl_013u_iob() throws DecoderException {
        // $ PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461589420
        // ./scripts/get-single-opcode.py '03399e393d6e0000009d290000016e0000000000 023900000b00a100820000000000000000000000 013900000000ac4b831b7a0b7cfc14a30b9c3995 0039dc8bbbdfa2ce2ce995725407'

        // TimeSinceResetResponse[pumpTime=1079514,timeSinceReset=461589420]
        initPumpState("6VeDeRAL5DCigGw2", 461589420L);
        InitiateBolusRequest expected = new InitiateBolusRequest(
                stripHmacCargo(new byte[]{110,0,0,0,-99,41,0,0,1,110,0,0,0,0,0,0,0,11,0,-95,0,-126,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-84,75,-125,27,122,11,124,-4,20,-93,11,-100,57,-107,-36,-117,-69,-33,-94,-50,44,-23,-107,114}));

        InitiateBolusRequest parsedReq = (InitiateBolusRequest) MessageTester.test(
                "03399e393d6e0000009d290000016e0000000000",
                57,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "023900000b00a100820000000000000000000000",
                "013900000000ac4b831b7a0b7cfc14a30b9c3995",
                "0039dc8bbbdfa2ce2ce995725407"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(110, parsedReq.getTotalVolume()); // 0.11u
        assertEquals(10653, parsedReq.getBolusID());
        assertEquals(1, parsedReq.getBolusTypeBitmask());
        assertEquals(Set.of(BolusDeliveryHistoryLog.BolusType.FOOD1), parsedReq.getBolusTypes());
        assertEquals(110, parsedReq.getFoodVolume()); // 0.11u
        assertEquals(0, parsedReq.getCorrectionVolume());
        assertEquals(11, parsedReq.getBolusCarbs());
        assertEquals(161, parsedReq.getBolusBG());
        assertEquals(130, parsedReq.getBolusIOB()); // 0.13u
    }

    @Test
    public void testInitiateBolusRequest_ID10677() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);
        InitiateBolusRequest expected = new InitiateBolusRequest(770, 10677, 3, 50, 720, 5, 185, 0);

        InitiateBolusRequest parsedReq = (InitiateBolusRequest) MessageTester.test(
                "03ab9eab3d02030000b52900000332000000d002",
                -85,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "02ab00000500b900000000000000000000000000",
                "01ab000000004123851bd5302d47de4038f56320",
                "00abce9113be8ce2a1e1b82126c5"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testInitiateBolusRequest_ID10678() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200296,timeSinceReset=461710202]
        initPumpState("6VeDeRAL5DCigGw2", 461710202L);
        InitiateBolusRequest expected = new InitiateBolusRequest(760, 10678, 3, 30, 730, 3, 186, 0);

        InitiateBolusRequest parsedReq = (InitiateBolusRequest) MessageTester.test(
                "03d09ed03df8020000b6290000031e000000da02",
                -48,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "02d000000300ba00000000000000000000000000",
                "01d0000000007a23851bcd0708af0ac0a24f2ee7",
                "00d0fa056febc1e4710541765047"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testInitiateBolusRequest_Mobi_Extended() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512442842,pumpTimeSinceReset=1905413,cargo={-38,65,-117,30,5,19,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905413L);

        // 0.5, 50/50, 2h
        // units, percent now/later, duration
        // -6,0,0,0,-11,1,0,0,12,0,0,0,0,0,0,0,0,0,0,-86,0,-6,10,0,0,0 ,0,0,0,0 ,0 ,0,0,0,0,0,0 < Base
        // -6,0,0,0,-11,1,0,0,12,0,0,0,0,0,0,0,0,0,0,-86,0,-6,10,0,0,-6,0,0,0,32,28,0,0,0,0,0,0 < Extended
        // extendedLaterVolume = -6,0,0,0 = 250
        // extendedLaterSeconds = 32,28,0,0 = 7200 = 2 hours
        // extended3 = 0,0,0,0 = 0 = presumably indicator of percent
        //
        InitiateBolusRequest expected = new InitiateBolusRequest(250, 501, 12, 0, 0, 0, 170, 2810, 250, 7200, 0);

        InitiateBolusRequest parsedReq = (InitiateBolusRequest) MessageTester.test(
                // Untitled_2_Live_-_Humans_iPhone
                "030d9e0d3dfa000000f50100000c000000000000",
                13,
                3,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "020d00000000aa00fa0a0000fa000000201c0000",
                "010d00000000b4968b1eae36a1f4be8a1069db07",
                "000d5f7074e1705b443ff533986f"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(250, parsedReq.getExtendedVolume());
        assertEquals(7200, parsedReq.getExtendedSeconds());

    }

    private byte[] stripHmacCargo(byte[] cargo) {
        return Bytes.dropLastN(cargo, 24);
    }
}