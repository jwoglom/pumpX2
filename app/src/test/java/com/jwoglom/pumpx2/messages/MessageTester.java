package com.jwoglom.pumpx2.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.events.PumpResponseMessageEvent;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import timber.log.Timber;

public class MessageTester {
    private static final String TAG = "X2-MessageTester";

    public static Message test(String rawHex, int txId, int expectedPackets, UUID expectedCharacteristic, Message expected) throws DecoderException {
        byte[] initialRead = Hex.decodeHex(rawHex);

        UUID uuid = CharacteristicUUID.determine(expected);
        assertEquals(uuid, expectedCharacteristic);

        MessageType messageType = expected.type();

        TronMessageWrapper tron = new TronMessageWrapper(expected, (byte) txId);
        PumpResponseMessageEvent resp = BTResponseParser.parse(tron, initialRead, messageType, uuid);
        assertTrue("Response message returned from parser: " + resp, resp.message().isPresent());

        Message parsedMessage = resp.message().get();
        assertEquals(parsedMessage.getClass(), expected.getClass());

        assertEquals(expectedPackets, tron.packets().size());
        byte[] mergedPackets = tron.mergeIntoSinglePacket().build();
        assertHexEquals(initialRead, mergedPackets);

        L.w(TAG, String.format("Parsed: %s\nExpected: %s", parsedMessage, expected));

        assertHexEquals(parsedMessage.getCargo(), expected.getCargo());

        return parsedMessage;
    }


    public static Message testMultiplePackets(List<String> rawHex, int txId, UUID expectedCharacteristic, Message expected) throws DecoderException {
        List<byte[]> initialReads = rawHex.stream().map(i -> {
            try {
                return Hex.decodeHex(i);
            } catch (DecoderException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        UUID uuid = CharacteristicUUID.determine(expected);
        assertEquals(uuid, expectedCharacteristic);

        MessageType messageType = expected.type();

        TronMessageWrapper tron = new TronMessageWrapper(expected, (byte) txId);
        PumpResponseMessageEvent resp = null;
        PacketArrayList packetArrayList = null;
        for (byte[] read : initialReads) {
            // Use the same PacketArrayList between packets
            if (resp == null) {
                packetArrayList = tron.buildPacketArrayList(messageType);
            }
            resp = BTResponseParser.parse(tron.message(), packetArrayList, read, uuid);
        }
        assertTrue("Response message returned from parser: " + resp, resp.message().isPresent());

        Message parsedMessage = resp.message().get();
        assertEquals(parsedMessage.getClass(), expected.getClass());

        assertEquals(rawHex.size(), tron.packets().size());
        for (int i=0; i<rawHex.size(); i++) {
            assertHexEquals(initialReads.get(i), tron.packets().get(i).build());
        }

        assertHexEquals(parsedMessage.getCargo(), expected.getCargo());

        return parsedMessage;
    }


    public static void assertHexEquals(byte[] a, byte[] b) {
        assertNotNull("first byte[] is null", a);
        assertNotNull("second byte[] is null", b);
        assertEquals(Hex.encodeHexString(a), Hex.encodeHexString(b));
    }
}
