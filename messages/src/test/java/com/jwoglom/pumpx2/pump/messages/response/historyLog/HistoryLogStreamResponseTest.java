package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import org.apache.commons.codec.DecoderException;
import com.jwoglom.pumpx2.shared.Hex;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class HistoryLogStreamResponseTest {
    @Test
    public void testHistoryLogStreamResponseId90() throws DecoderException {
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
            // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                1, 4,
                new ArrayList<>(ImmutableList.of(new byte[]{90,0,-128,13,-108,26,0,0,0,0,0,0,0,0,-102,-35,32,2,0,0,0,0,0,0,0,0}))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c01045a00800d941a00000000000000009add200200000000000000004834",
                0,
                2,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(90, Bytes.readShort(expected.getCargo(), 2));
        byte[] stream = expected.getHistoryLogStreamBytes().stream().findFirst().get();
        assertEquals(90, Bytes.readShort(stream, 0));
    }

    @Test
    public void testHistoryLogStreamResponseId23() throws DecoderException {
        // seq number 2869
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                1, 10,
                new ArrayList<>(ImmutableList.of(new byte[]{23,0,43,-55,-77,26,53,11,0,0,100,0,0,0,100,0,0,0,1,0,0,0,-11,118,92,0}))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c010a17002bc9b31a350b0000640000006400000001000000f5765c003b5c",
                0,
                2,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(23, Bytes.readShort(expected.getCargo(), 2));
        byte[] stream = expected.getHistoryLogStreamBytes().stream().findFirst().get();
        assertEquals(23, Bytes.readShort(stream, 0));
    }

    @Test
    public void testHistoryLogStreamResponseId17() throws DecoderException {
        // seq number 2870
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                1, 11,
                new ArrayList<>(ImmutableList.of(new byte[]{17,0,84,-55,-77,26,54,11,0,0,3,24,93,0,1,0,0,0,0,0,0,0,0,0,0,0}))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c010b110054c9b31a360b000003185d000100000000000000000000006e81",
                0,
                2,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(17, Bytes.readShort(expected.getCargo(), 2));
        byte[] stream = expected.getHistoryLogStreamBytes().stream().findFirst().get();
        assertEquals(17, Bytes.readShort(stream, 0));
    }

    @Test
    public void testHistoryLogStreamResponseId17_2() throws DecoderException {
        // seq number 2871
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                1, 12,
                new ArrayList<>(ImmutableList.of(new byte[]{17,0,85,-55,-77,26,55,11,0,0,-61,24,93,0,0,0,0,0,0,0,0,0,0,0,0,0}))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c010c110055c9b31a370b0000c3185d00000000000000000000000000c06e",
                0,
                2,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(17, Bytes.readShort(expected.getCargo(), 2));
        byte[] stream = expected.getHistoryLogStreamBytes().stream().findFirst().get();
        assertEquals(17, Bytes.readShort(stream, 0));
    }

    @Test
    public void testHistoryLogStreamResponseId22() throws DecoderException {
        // seq number 2872
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                1, 13,
                new ArrayList<>(ImmutableList.of(new byte[]{22,0,86,-55,-77,26,56,11,0,0,100,0,0,0,100,0,0,0,1,0,0,0,-17,29,93,0}))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c010d160056c9b31a380b0000640000006400000001000000ef1d5d00a77d",
                0,
                2,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(22, Bytes.readShort(expected.getCargo(), 2));
        byte[] stream = expected.getHistoryLogStreamBytes().stream().findFirst().get();
        assertEquals(22, Bytes.readShort(stream, 0));
    }

    @Test
    public void testHistoryLogStreamResponseId10() throws DecoderException {
        // seq number 2873
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                1, 14,
                new ArrayList<>(ImmutableList.of(new byte[]{10,0,87,-55,-77,26,57,11,0,0,1,3,0,0,-33,30,93,0,1,32,93,0,52,33,93,0}))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c010e0a0057c9b31a390b000001030000df1e5d0001205d0034215d000c57",
                0,
                2,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(10, Bytes.readShort(expected.getCargo(), 2));
        byte[] stream = expected.getHistoryLogStreamBytes().stream().findFirst().get();
        assertEquals(10, Bytes.readShort(stream, 0));
    }

    @Test
    public void testHistoryLogStreamResponseId81() throws DecoderException {
        // seq number 2874
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                1, 15,
                new ArrayList<>(ImmutableList.of(new byte[]{81,0,95,-55,-77,26,58,11,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,100,112,16}))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c010f51005fc9b31a3a0b00000000000000000000000000000164701091ea",
                0,
                2,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(81, Bytes.readShort(expected.getCargo(), 2));
        byte[] stream = expected.getHistoryLogStreamBytes().stream().findFirst().get();
        assertEquals(81, Bytes.readShort(stream, 0));
    }

    @Test
    @Ignore("fix historyLog")
    public void testHistoryLogStreamResponseId13_TimeChange() throws DecoderException {
        // seq number 2875
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                1, 16,
                new ArrayList<>(ImmutableList.of(new byte[]{13,0,28,-54,-77,26,59,11,0,0,115,66,93,0,96,33,96,0,5,-97,96,50,0,0,0,0}))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c01100d001ccab31a3b0b000073425d0060216000059f60320000000033f9",
                0,
                2,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(13, Bytes.readShort(expected.getCargo(), 2));
        byte[] stream = expected.getHistoryLogStreamBytes().stream().findFirst().get();
        assertEquals(13, Bytes.readShort(stream, 0));

        List<HistoryLog> logs = parsedRes.getHistoryLogs();
        assertEquals(1, logs.size());
        assertTrue(logs.get(0) instanceof TimeChangedHistoryLog);

        TimeChangedHistoryLog event = (TimeChangedHistoryLog) logs.get(0);
//        assertEquals(1114832896L, event.getTimePrior());
//        assertEquals(559939677L, event.getTimeAfter());
//        assertEquals(2667905120L, event.getRawRTC());
    }


    @Test
    public void testHistoryLogStreamResponse_MultiStream_1() throws DecoderException {
        // seq number 2875
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                3, 8,
                new ArrayList<>(ImmutableList.of(
                        Hex.decodeHex("07013352331b6e0f000013010000000000000000000000000000"), // typeId 263
                        Hex.decodeHex("db002751331b6d0f000000000000000000000000000006000000"), // typeId 219
                        Hex.decodeHex("ed002651331b6c0f000000000000000000000000000000000000") // typeId 237
                ))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "0000810050030807013352331b6e0f000013010000000000000000000000000000db002751331b6d0f000000000000000000000000000006000000ed002651331b6c0f000000000000000000000000000000000000c651",
                0,
                5,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testHistoryLogStreamResponse_MultiStream_2() throws DecoderException {
        // seq number 2875
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                3, 8,
                new ArrayList<>(ImmutableList.of(
                        Hex.decodeHex("17012651331b6b0f00000000000000002003ffffffff00000000"), // typeId 279
                        Hex.decodeHex("db00fb4f331b6a0f000000000000000000000000000006000000"), // typeId 219
                        Hex.decodeHex("1701fa4f331b690f00000000000000002003ffffffff00000000") // typeId 279
                ))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "0000810050030817012651331b6b0f00000000000000002003ffffffff00000000db00fb4f331b6a0f0000000000000000000000000000060000001701fa4f331b690f00000000000000002003ffffffff00000000cfc8",
                0,
                5,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testHistoryLogStreamResponse_MultiStream_3() throws DecoderException {
        // seq number 2875
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                3, 8,
                new ArrayList<>(ImmutableList.of(
                        Hex.decodeHex("db00cf4e331b680f000000000000000000000000000006000000"), // typeId 219
                        Hex.decodeHex("1701ce4e331b670f00000000000000002003ffffffff00000000"), // typeId 279
                        Hex.decodeHex("08011a4e331b660f0000100100000d0408000000000000000000") // typeId 264
                ))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "00008100500308db00cf4e331b680f0000000000000000000000000000060000001701ce4e331b670f00000000000000002003ffffffff0000000008011a4e331b660f0000100100000d04080000000000000000000614",
                0,
                5,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testHistoryLogStreamResponse_MultiStream_4() throws DecoderException {
        // seq number 2875
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                1, 8,
                new ArrayList<>(ImmutableList.of(
                        Hex.decodeHex("0801ca4d331b650f0000100100000d0408000000000000000000") // typeId 264
                ))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c01080801ca4d331b650f0000100100000d0408000000000000000000b442",
                0,
                2,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }

    @Test
    public void testHistoryLogStreamResponse_MultiStream_5() throws DecoderException {
        // seq number 2875
        HistoryLogStreamResponse expected = new HistoryLogStreamResponse(
                // int numberOfHistoryLogs, int streamId, List<byte[]> historyLogStreams
                3, 40,
                new ArrayList<>(ImmutableList.of(
                        new byte[]{-37, 16, -98, -51, 39, 32, 73, -98, 1, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0},
                        new byte[]{44, 17, -98, -51, 39, 32, 72, -98, 1, 0, -123, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        new byte[]{34, 17, -98, -51, 39, 32, 71, -98, 1, 0, -39, 19, 40, 32, -124, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                ))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "00008100500328db109ecd2720499e0100020000000200000000000000050000002c119ecd2720489e01008500000000000000000000000000000022119ecd2720479e0100d9132820840000000000000000000000676e",
                0,
                5,
                CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}