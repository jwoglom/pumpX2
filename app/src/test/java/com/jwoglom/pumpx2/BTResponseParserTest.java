package com.jwoglom.pumpx2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.bluetooth.*;
import com.jwoglom.pumpx2.pump.events.BTProcessGattOperationEvent;
import com.jwoglom.pumpx2.pump.events.PumpResponseMessageEvent;
import com.jwoglom.pumpx2.pump.messages.*;
import com.jwoglom.pumpx2.pump.messages.request.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.request.CentralChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.request.PumpChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.response.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.PumpChallengeResponse;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BTResponseParserTest {
    private static final String TAG = "X2-BtResponseParserTest";


    @Test
    public void testTconnectAppFirstRequest() throws DecoderException {
        //                                        01011201160000bc006b513d27b69a0153968e67
        byte[] initialWrite = Hex.decodeHex("000010000a00004d08435da26947356d6f");
        byte[] initialResponse = Hex.decodeHex("000011001e01008c212d7a8fbda85f83a3440254488dfb561264ec840c4e16873046bc2c1a");

        TronMessageWrapper wrapper = new TronMessageWrapper(new CentralChallengeRequest(0, new byte[]{0,1,2,3,4,5,6,7,8,9}), (byte) 0);
        PumpResponseMessageEvent response = BTResponseParser.parse(wrapper, initialResponse, MessageType.RESPONSE, CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS);
        L.w(TAG, "PumpResponseMessageEvent: "+response);

        assertTrue(response.message().isPresent());
        assertTrue(response.message().get() instanceof CentralChallengeResponse);
        CentralChallengeResponse message = (CentralChallengeResponse) response.message().get();
        L.w(TAG, "CentralChallengeResponse: "+message);

        assertFalse(message.signed());
        assertEquals(1, message.getByte0short());
        assertEquals("8c212d7a8fbda85f83a3440254488dfb561264ec", Hex.encodeHexString(message.getBytes2to22()));
        assertEquals("840c4e16873046bc", Hex.encodeHexString(message.getBytes22to30()));

    }


    // Broken test -- bad input?
    @Test
    public void testPumpChallengeResponse() throws DecoderException {
        byte[] challengeResponse = Hex.decodeHex("010012001600008ba3e0de472d81b9143ea53733");
        PumpChallengeRequest request = new PumpChallengeRequest(0, Packetize.doHmacSha1(new byte[0],"FtBZ7ikz3fQPZxmV".getBytes(StandardCharsets.UTF_8)));

        TronMessageWrapper wrapper = new TronMessageWrapper(request, (byte) 0);
        PumpResponseMessageEvent response = BTResponseParser.parse(wrapper, challengeResponse, MessageType.RESPONSE, CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS);
        L.w(TAG, "PumpChallengeResponseEvent: "+response);

        assertTrue(response.message().isPresent());
        assertTrue(response.message().get() instanceof PumpChallengeResponse);
        PumpChallengeResponse message = (PumpChallengeResponse) response.message().get();
        L.w(TAG, "PumpChallengeResponse: "+message);
    }

    @Test
    public void testParserApiVersionRequest() {
        // BT WRITE uuid: 7b83fff6-9f77-4e5c-8064-aae2c24838b9 data: 2000005a4a
        // BT READ output: BT READ output: 2000005a4a
    }

    @Test
    public void testCentralChallengeRequest() throws DecoderException {
        byte[] centralChallenge = Hex.decodeHex("670b42de65985e97");
        CentralChallengeRequest request = new CentralChallengeRequest(0, centralChallenge);

        Packetize.txId = new TransactionId();
        TronMessageWrapper wrapper = new TronMessageWrapper(request, (byte) 0);

        assertEquals("000010000a0000670b42de65985e97f1ce", Hex.encodeHexString(wrapper.packets().get(0).build()));
    }

    @Test
    public void testCentralChallengeResponse() throws DecoderException {
        byte[] write = Hex.decodeHex("000010000a0000670b42de65985e97f1ce");
        byte[] read = Hex.decodeHex("000011001e01001f80d667645fe56c0d64575b9d07bb5f28392cab6079f224bf1aa8fd4359");

        TronMessageWrapper wrapper = new TronMessageWrapper(new CentralChallengeRequest(0, new byte[]{0,1,2,3,4,5,6,7,8,9}), (byte) 0);
        PumpResponseMessageEvent response = BTResponseParser.parse(wrapper, read, MessageType.RESPONSE, CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS);
        L.w(TAG, "PumpResponseMessageEvent: "+response);

        assertTrue(response.message().isPresent());
        assertTrue(response.message().get() instanceof CentralChallengeResponse);
        CentralChallengeResponse message = (CentralChallengeResponse) response.message().get();
        L.w(TAG, "CentralChallengeResponse: "+message);

        assertFalse(message.signed());
        assertEquals(1, message.getByte0short());
        assertEquals("1f80d667645fe56c0d64575b9d07bb5f28392cab", Hex.encodeHexString(message.getBytes2to22()));
        assertEquals("6079f224bf1aa8fd", Hex.encodeHexString(message.getBytes22to30()));
    }

    @Test
    public void testRawBytesToWrite() throws DecoderException {
        List<byte[]> writeBytes;
        {
            Message message = new CentralChallengeRequest(0, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9});

            byte currentTxId = Packetize.txId.get();
            TronMessageWrapper wrapper = new TronMessageWrapper(message, currentTxId);
            Packetize.txId.increment();
            UUID uuid = CharacteristicUUID.determine(message);

            boolean multiplePackets = wrapper.packets().size() > 1;
            L.w(TAG, "SendPumpMessage packets: " + wrapper.packets());

            BTProcessGattOperationEvent btEvent = null;
            writeBytes = new ArrayList<>();
            List<BTProcessGattOperationEvent> events = new ArrayList<>();
            for (Packet packet : wrapper.packets()) {
                L.w(TAG, "SendPumpMessage packet: " + packet);
                btEvent = new BTProcessGattOperationEvent(uuid, packet.build(), multiplePackets);
                writeBytes.add(btEvent.data());
                events.add(btEvent);
            }
        }

        assertEquals(1, writeBytes.size());
        assertEquals("000010000a00000001020304050607361a", Hex.encodeHexString(writeBytes.get(0)));

        {
            Message message = new PumpChallengeRequest(0, Packetize.doHmacSha1(new byte[0],"Yn9jrWuF8HLCdPSn".getBytes(StandardCharsets.UTF_8)));

            byte currentTxId = Packetize.txId.get();
            TronMessageWrapper wrapper = new TronMessageWrapper(message, currentTxId);
            Packetize.txId.increment();
            UUID uuid = CharacteristicUUID.determine(message);

            boolean multiplePackets = wrapper.packets().size() > 1;
            L.w(TAG, "SendPumpMessage packets: " + wrapper.packets());

            BTProcessGattOperationEvent btEvent = null;
            writeBytes = new ArrayList<>();
            List<BTProcessGattOperationEvent> events = new ArrayList<>();
            for (Packet packet : wrapper.packets()) {
                L.w(TAG, "SendPumpMessage packet: " + packet);
                btEvent = new BTProcessGattOperationEvent(uuid, packet.build(), multiplePackets);
                writeBytes.add(btEvent.data());
                events.add(btEvent);
            }
        }

        assertEquals(2, writeBytes.size());
        assertEquals("010112011600004dc561ac26081e7afa4f374196", Hex.encodeHexString(writeBytes.get(0)));
        assertEquals("000115687fa4cb337ac44c", Hex.encodeHexString(writeBytes.get(1)));

        {
            Message message = new PumpChallengeRequest(0, Packetize.doHmacSha1(new byte[0],"DhVGgUqqPfwx56HL".getBytes(StandardCharsets.UTF_8)));

            byte currentTxId = Packetize.txId.get();
            TronMessageWrapper wrapper = new TronMessageWrapper(message, currentTxId);
            Packetize.txId.increment();
            UUID uuid = CharacteristicUUID.determine(message);

            boolean multiplePackets = wrapper.packets().size() > 1;
            L.w(TAG, "SendPumpMessage packets: " + wrapper.packets());

            BTProcessGattOperationEvent btEvent = null;
            writeBytes = new ArrayList<>();
            List<BTProcessGattOperationEvent> events = new ArrayList<>();
            for (Packet packet : wrapper.packets()) {
                L.w(TAG, "SendPumpMessage packet: " + packet);
                btEvent = new BTProcessGattOperationEvent(uuid, packet.build(), multiplePackets);
                writeBytes.add(btEvent.data());
                events.add(btEvent);
            }
        }

        assertEquals(2, writeBytes.size());
        assertEquals("010212021600009a6cf6348337f61a47217d6d1d", Hex.encodeHexString(writeBytes.get(0)));
        assertEquals("00023c0d74a78f44531dc9", Hex.encodeHexString(writeBytes.get(1)));

        // ApiVersionRequest needs auth
        {
            Message message = new ApiVersionRequest();

            byte currentTxId = Packetize.txId.get();
            TronMessageWrapper wrapper = new TronMessageWrapper(message, currentTxId);
            Packetize.txId.increment();
            UUID uuid = CharacteristicUUID.determine(message);

            boolean multiplePackets = wrapper.packets().size() > 1;
            L.w(TAG, "SendPumpMessage packets: " + wrapper.packets());

            BTProcessGattOperationEvent btEvent = null;
            writeBytes = new ArrayList<>();
            List<BTProcessGattOperationEvent> events = new ArrayList<>();
            for (Packet packet : wrapper.packets()) {
                L.w(TAG, "SendPumpMessage packet: " + packet);
                btEvent = new BTProcessGattOperationEvent(uuid, packet.build(), multiplePackets);
                writeBytes.add(btEvent.data());
                events.add(btEvent);
            }
        }

        assertEquals(1, writeBytes.size());
        assertEquals("0003200300091f", Hex.encodeHexString(writeBytes.get(0)));
    }

    @Test
    public void testRawBytesRead() throws DecoderException {
        /*
        000011001e01006b3ace31b3f24b8d424e13ebf9c344b31e44d26d37a5efcde10e7ec671cb

        0001130103010000c9dc
         */

        {
            byte[] centralChallengeResponse = Hex.decodeHex("000011001e01006b3ace31b3f24b8d424e13ebf9c344b31e44d26d37a5efcde10e7ec671cb");

            TronMessageWrapper wrapper = new TronMessageWrapper(new CentralChallengeRequest(0, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}), (byte) 0);
            PumpResponseMessageEvent response = BTResponseParser.parse(wrapper, centralChallengeResponse, MessageType.RESPONSE, CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS);
            L.w(TAG, "PumpResponseMessageEvent: " + response);

            assertTrue(response.message().isPresent());
            assertTrue(response.message().get() instanceof CentralChallengeResponse);
            CentralChallengeResponse message = (CentralChallengeResponse) response.message().get();
            L.w(TAG, "CentralChallengeResponse: " + message);

            assertFalse(message.signed());
            assertEquals(1, message.getByte0short());
            assertEquals("6b3ace31b3f24b8d424e13ebf9c344b31e44d26d", Hex.encodeHexString(message.getBytes2to22()));
            assertEquals("37a5efcde10e7ec6", Hex.encodeHexString(message.getBytes22to30()));
        }

        {
            // Failed challenge
            byte[] pumpChallengeResponse = Hex.decodeHex("0001130103010000c9dc");

            TronMessageWrapper wrapper = new TronMessageWrapper(new PumpChallengeRequest(0, new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9}), (byte) 1);
            PumpResponseMessageEvent response = BTResponseParser.parse(wrapper, pumpChallengeResponse, MessageType.RESPONSE, CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS);
            L.w(TAG, "PumpResponseMessageEvent: " + response);

            assertTrue(response.message().isPresent());
            assertTrue(response.message().get() instanceof PumpChallengeResponse);
            PumpChallengeResponse message = (PumpChallengeResponse) response.message().get();
            L.w(TAG, "PumpChallengeResponse: " + message);

            assertFalse(message.signed());
            assertFalse(message.getSuccess());
        }
    }
}