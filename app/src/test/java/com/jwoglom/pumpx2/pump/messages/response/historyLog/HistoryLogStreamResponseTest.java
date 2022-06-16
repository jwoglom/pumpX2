package com.jwoglom.pumpx2.pump.messages.response.historyLog;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.util.ArrayList;

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
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
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
                1, 4,
                new ArrayList<>(ImmutableList.of(new byte[]{23,0,43,-55,-77,26,53,11,0,0,100,0,0,0,100,0,0,0,1,0,0,0,-11,118,92,0}))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c010a17002bc9b31a350b0000640000006400000001000000f5765c003b5c",
                0,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
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
                1, 4,
                new ArrayList<>(ImmutableList.of(new byte[]{17,0,84,-55,-77,26,54,11,0,0,3,24,93,0,1,0,0,0,0,0,0,0,0,0,0,0}))
        );

        HistoryLogStreamResponse parsedRes = (HistoryLogStreamResponse) MessageTester.test(
                "000081001c010b110054c9b31a360b000003185d000100000000000000000000006e81",
                0,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
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
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
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
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
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
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
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
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(81, Bytes.readShort(expected.getCargo(), 2));
        byte[] stream = expected.getHistoryLogStreamBytes().stream().findFirst().get();
        assertEquals(81, Bytes.readShort(stream, 0));
    }

    @Test
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
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());

        assertEquals(81, Bytes.readShort(expected.getCargo(), 2));
        byte[] stream = expected.getHistoryLogStreamBytes().stream().findFirst().get();
        assertEquals(81, Bytes.readShort(stream, 0));
    }
}