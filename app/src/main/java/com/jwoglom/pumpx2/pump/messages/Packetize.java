package com.jwoglom.pumpx2.pump.messages;

import com.google.common.collect.Lists;
import com.jwoglom.pumpx2.pump.PumpState;
import com.jwoglom.pumpx2.pump.bluetooth.models.Packet;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.text.Charsets;

public class Packetize {
    public static TransactionId txId = new TransactionId();

    public static List<Packet> packetize(Message message, String authenticationKey, byte currentTxId) {
        return packetize(message, authenticationKey, currentTxId, 18);
    }

    public static List<Packet> packetize(Message message, String authenticationKey, byte currentTxId, int maxChunkSize) {
        int length = 3 + message.getCargo().length;
        if (message.signed()) {
            length += 24;
        }
        byte[] packet = new byte[length];
        packet[0] = (byte) message.opCode();
        packet[1] = currentTxId;
        packet[2] = (byte) (length - 3);
        // packet[3 ... N] filled with message cargo
        System.arraycopy(message.getCargo(), 0, packet, 3, message.getCargo().length);

        if (message.signed()) {
            byte[] hmacShaData = new byte[length - 20];
            byte[] timeSinceReset = Bytes.toUint32(PumpState.timeSinceReset);
            // packet[0 .. N-20] unchanged
            System.arraycopy(packet, 0, hmacShaData, 0, length - 20);
            // packet[N-24 .. N-20] filled with time since reset
            System.arraycopy(timeSinceReset, 0, hmacShaData, length - 24, 4);
            byte[] hmacByteKey = authenticationKey.getBytes(Charsets.UTF_8);
            byte[] hmacSha1Output = doHmacSha1(hmacShaData, hmacByteKey);

            // packet[N-20 ... N] filled with hmac sha1 output
            System.arraycopy(hmacSha1Output, 0, packet, length - 20, hmacSha1Output.length);
        }

        // Append CRC to packet
        byte[] crc = Bytes.calculateCRC16(packet);
        byte[] packetWithCRC = ArrayUtils.addAll(packet, crc);

        // Fill Packet list with chunks of size 18 (maxChunkSize)
        List<Packet> packets = new ArrayList<>();
        List<List<Byte>> chunked = Lists.partition(Arrays.asList(ArrayUtils.toObject(packetWithCRC)), maxChunkSize);

        int b = chunked.size() - 1;
        for (List<Byte> bytes : chunked) {
            byte[] elem = ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
            packets.add(new Packet((byte) b, currentTxId, elem));
            b--;
        }

        return packets;
    }

    public static byte[] doHmacSha1(byte[] data, byte[] key) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmac(data);
    }
}
