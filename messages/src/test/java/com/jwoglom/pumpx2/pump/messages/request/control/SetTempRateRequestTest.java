package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class SetTempRateRequestTest {
    @Test
    public void testSetTempRateRequest_0pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 0
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, 0, 0}, expected.getCargo());

        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "0151a4511ea0bb0d0000005f6ef01fd2ed2a3276",
                81,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "005179ee6512fde74533e1f1b7d863347d27bf"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_1pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 1
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, 1, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "017da47d1ea0bb0d000100936ef01fa736f5a248",
                125,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "007d1af9111b009e52e04b30d22241635de6bf"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(1, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_2pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 2
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, 2, 0}, expected.getCargo());

        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "0190a4901ea0bb0d000200ab6ef01f8add3e9729",
                -112,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0090fde703c39a6278e3f2fd2a061b9c8dd8ee"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(2, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_10pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 10
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, 10, 0}, expected.getCargo());

        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01a2a4a21ea0bb0d000a00bc6ef01f0c373adbe4",
                -94,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00a273ec2f89dc1ce285547b87c0bb95138a64"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(10, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_40pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 40
        );
        assertHexEquals(new byte[]{-96, -69, 13, 0, 40, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01b5a4b51ea0bb0d002800d46ef01fc26d64aeb1",
                -75,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00b5c5a1530bf58d12e57e4f1bf1699c8f3adf"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(40, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_50pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 50
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, 50, 0}, expected.getCargo());

        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01c8a4c81ea0bb0d003200e66ef01fcea48f2fac",
                -56,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00c826336f9f6a8f34304818abf9e13ca37408"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(50, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_51pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 51
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, 51, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01d8a4d81ea0bb0d003300fd6ef01f1ad8f7953c",
                -40,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00d852535070f4e7bfeb0b02bb64144c15b6f4"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(51, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_52pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 52
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, 52, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01e7a4e71ea0bb0d0034000a6ff01f3d7fba8667",
                -25,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00e7cc160c89eb7201e359591d165fcdf8bebe"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(52, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_98pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 98
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, 98, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01fba4fb1ea0bb0d0062001f6ff01f7d67b16536",
                -5,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00fbea93dfed8f17eb822192e68b924fd723df"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(98, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_140pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 140
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, -116, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "010da40d1ea0bb0d008c00316ff01f5a4be23492",
                13,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "000dfab7fdac6cfc377fcbe1d3aa7d6bc6bb05"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(140, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_180pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 180
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, -76, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "011da41d1ea0bb0d00b400406ff01f9d6f9efde6",
                29,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "001d9f6a71538a5b2a3add07b39a91fa759fba"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(180, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_199pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 199
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, -57, 0}, expected.getCargo());

        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "0131a4311ea0bb0d00c700526ff01febde639dbf",
                49,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "003157339f103c7394932d70ed467524abc61d"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(199, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_201pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 201
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, -55, 0}, expected.getCargo());

        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "0143a4431ea0bb0d00c900676ff01f7ebca592bc",
                67,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0043916ac521eded9e84d122e7b75e7a054586"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(201, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_245pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 245
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, -11, 0}, expected.getCargo());

        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "0155a4551ea0bb0d00f5007c6ff01f24b05dac9b",
                85,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0055e618d35684b87d8cce8c356302839098ae"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(245, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_250pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                15, 250
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, -6, 0}, expected.getCargo());

        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "0167a4671ea0bb0d00fa00896ff01fc697a2ed82",
                103,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00674d86c93d7fcc55a1a57c9a6e6b7c734b6c"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(250, expected.getPercent());
        assertEquals(15, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_0pct_16min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                // 16, 0
                new byte[]{0, -90, 14, 0, 0, 0}
        );

        assertHexEquals(new byte[]{0, -90, 14, 0, 0, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "017aa47a1e00a60e0000009c6ff01fd518027a01",
                122,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "007a9e3f8b12952dd41b013580028bd358981a"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, expected.getPercent());
        assertEquals(16, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_0pct_17min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                // 17, 0
                new byte[]{96, -112, 15, 0, 0, 0}
        );

        assertHexEquals(new byte[]{96, -112, 15, 0, 0, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "018aa48a1e60900f000000ae6ff01fe6475f9d9b",
                -118,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "008a1c9e1920361d6075162964e5bee8b87eac"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, expected.getPercent());
        assertEquals(17, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_0pct_18min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                // 18, 0
                new byte[]{-64, 122, 16, 0, 0, 0}
        );

        assertHexEquals(new byte[]{-64, 122, 16, 0, 0, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "019ca49c1ec07a10000000c46ff01faa229302d8",
                -100,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "009cc3413fc22ebcaaa8b6919ca85c526bddca"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(0, expected.getPercent());
        assertEquals(18, expected.getMinutes());
    }

    @Test
    public void testSetTempRateRequest_95pct_59min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                // 59, 95
                new byte[]{32, 4, 54, 0, 95, 0}
        );

        assertHexEquals(new byte[]{32, 4, 54, 0, 95, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01afa4af1e200436005f00df6ff01f128b115990",
                -81,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00afd1c77c1e849f7852275c0a1bc29c8d1956"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(95, expected.getPercent());
        assertEquals(59, expected.getMinutes()); // -3
    }

    @Test
    public void testSetTempRateRequest_95pct_2h15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                // 135, 95
                new byte[]{-96, -104, 123, 0, 95, 0}
        );

        assertHexEquals(new byte[]{-96, -104, 123, 0, 95, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01bea4be1ea0987b005f00ec6ff01fdda86077e2",
                -66,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00be42130e21edebfb4193239c2a4f18b9de14"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(95, expected.getPercent());
        assertEquals(2*60 + 15, expected.getMinutes()); // -10
    }

    @Test
    public void testSetTempRateRequest_95pct_3h15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                // 195, 95
                new byte[]{32, -121, -78, 0, 95, 0}
        );

        assertHexEquals(new byte[]{32, -121, -78, 0, 95, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01d0a4d01e2087b2005f00fa6ff01fc5457a0f5f",
                -48,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00d02c5dff03f795611fa4d724e0c2e2f081ee"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(95, expected.getPercent());
        assertEquals(3*60 + 15, expected.getMinutes()); // -15
    }

    @Test
    public void testSetTempRateRequest_95pct_12h15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                // 735, 95
                new byte[]{-96, -23, -96, 2, 95, 0}
        );

        assertHexEquals(new byte[]{-96, -23, -96, 2, 95, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01e1a4e11ea0e9a0025f000e70f01fe0c7050329",
                -31,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00e1c2b68ba9ab21c5b2890fbdff983c65faa6"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(95, expected.getPercent());
        assertEquals(12*60 + 15, expected.getMinutes()); // -61
    }

    @Test
    public void testSetTempRateRequest_95pct_71h15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                // 735, 95
                new byte[]{32, -31, 73, 15, 95, 0}
        );

        assertHexEquals(new byte[]{32, -31, 73, 15, 95, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "01f3a4f31e20e1490f5f002470f01f8da7ffa546",
                -13,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00f33a8a503793fd1edeef7c7c63715a2e93d3"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(95, expected.getPercent());
        assertEquals(71*60 + 15, expected.getMinutes()); // -360
    }

    @Test
    public void testSetTempRateRequest_95pct_71h15min_2() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                // 735, 95
                new byte[]{32, -31, 73, 15, 95, 0}
        );

        assertHexEquals(new byte[]{32, -31, 73, 15, 95, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "0103a4031e20e1490f5f003670f01f44d97042ee",
                3,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "000306c26ba1833dd91cf172f3c2cb1d062486"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(95, expected.getPercent());
        assertEquals(71*60 + 15, expected.getMinutes()); // -360
    }

    @Test
    public void testSetTempRateRequest_95pct_72h() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
                // 4320, 95
                new byte[]{0, 20, 115, 15, 95, 0}
        );

        assertHexEquals(new byte[]{0, 20, 115, 15, 95, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                "0118a4181e0014730f5f004a70f01f79ae754dab",
                24,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00180002ed28543998f800552cd97030d29966"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(95, expected.getPercent());
        assertEquals(72*60, expected.getMinutes()); // -363
    }

    @Test
    public void testSetTempRateRequest_95pct_15min() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512443560,pumpTimeSinceReset=1906112,cargo={-88,68,-117,30,-64,21,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1906112L);

        // Temp Rate 95% for 15m
        SetTempRateRequest expected = new SetTempRateRequest(
                15, 95
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, 95, 0}, expected.getCargo());

        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                // Untitled_2_Live_-_Humans_iPhone 2
                // 2024-03-28T00:26:21.525000+00:00
                "0122a4221ea0bb0d005f001c998b1e37eacb5339",
                34,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00227c57675fc47f8c9bf5dec1f0d31d42a4b7"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testSetTempRateRequest_101pct_15min() throws DecoderException {
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 0L);

        SetTempRateRequest expected = new SetTempRateRequest(
            new byte[]{-96, -69, 13, 0, 101, 0}
        );

        assertHexEquals(new byte[]{-96, -69, 13, 0, 101, 0}, expected.getCargo());


        SetTempRateRequest parsedReq = (SetTempRateRequest) MessageTester.test(
                // 2024-12-22-mobi-screenrec.jsonl
                // 2024-12-22 09:46:31.486000
                "0148a4481ea0bb0d00650016eaee1fc5720c4461",
                72,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "0048b8e23a6c4fb71331d0cbf3547996eb6cb5"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}