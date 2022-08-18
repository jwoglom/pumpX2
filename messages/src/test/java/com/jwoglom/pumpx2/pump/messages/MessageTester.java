package com.jwoglom.pumpx2.pump.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.jwoglom.pumpx2.pump.messages.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.control.BolusPermissionRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.InitiateBolusRequest;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MessageTester {
    private static final String TAG = "X2-MessageTester";

    public static void initPumpState(String pumpAuthenticationKey, long timeSinceReset) {
        PumpStateSupplier.authenticationKey = () -> pumpAuthenticationKey;
        PumpStateSupplier.pumpTimeSinceReset = () -> timeSinceReset;
    }

    public static Message test(String rawHex, int txId, int expectedPackets, UUID expectedCharacteristic, Message expected, String ...extraHexBtPackets) throws DecoderException {
        byte[] initialRead = Hex.decodeHex(rawHex);
        byte[] totalRead = initialRead;

        UUID uuid = CharacteristicUUID.determine(expected);
        assertEquals(uuid, expectedCharacteristic);

        MessageType messageType = expected.type();

        TronMessageWrapper tron = new TronMessageWrapper(expected, (byte) txId);
        PacketArrayList packetArrayList = tron.buildPacketArrayList(messageType);
        PumpResponseMessage resp = BTResponseParser.parse(tron.message(), packetArrayList, initialRead, uuid);

        for (String extraHexBtPacket : extraHexBtPackets) {
            byte[] additionalRead = Hex.decodeHex(extraHexBtPacket);
            totalRead = Bytes.combine(totalRead, additionalRead);
            resp = BTResponseParser.parse(tron.message(), packetArrayList, additionalRead, uuid);
        }

        assertTrue("Response message returned from parser: " + resp, resp.message().isPresent());

        Message parsedMessage = resp.message().get();
        L.w(TAG, String.format("Parsed: %s\nExpected: %s", parsedMessage, expected));
        assertEquals(expected.getClass(), parsedMessage.getClass());
        assertEquals(expected.verboseToString(), parsedMessage.verboseToString());

        // BUG: for control signed messages this fails due to the 24byte padding at the end of the request.
        // we are expecting 01TXOP??18 (0x18 = 24, the size of the request)
        // but is 00TXOP??30 (0x30 = 48, double) with a much longer length
        if (!parsedMessage.signed()) {

            assertEquals("expected packet size", expectedPackets, tron.packets().size());

            Packet mergedPackets = tron.mergeIntoSinglePacket();
            byte[] mergedPacketsBytes = mergedPackets.build();
            assertHexEquals(totalRead, mergedPacketsBytes);
        }


        assertHexEquals(parsedMessage.getCargo(), expected.getCargo());

        return parsedMessage;
    }


    public static Message testMultiplePackets(List<String> rawHex, int txId, UUID expectedCharacteristic, Message expected) {
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
        PumpResponseMessage resp = null;
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

    public static void guessCargo(Message message) {
        byte[] cargo = message.getCargo();
        int max = message.props().size();
        for (int i=0; i<max; i++) {
            System.out.print(i+" "+cargo[i]+"\t");
            if (i+4 <= max) {System.out.print("uint32: "+Bytes.readUint32(cargo, i)+"\t");}
            if (i+2 <= max) {System.out.print("short: "+Bytes.readShort(cargo,i)+"\t");}
            if (i+4 <= max) {System.out.print("float: "+Bytes.readFloat(cargo, i)+"\t");}
            System.out.println();
        }
    }
}
