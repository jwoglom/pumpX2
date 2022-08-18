package com.jwoglom.pumpx2.pump.messages;

import com.google.common.collect.Lists;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.text.Charsets;

public class Packetize {
    public static final String TAG = "X2-Packetize";
    public static TransactionId txId = new TransactionId();

    public static final int DEFAULT_MAX_CHUNK_SIZE = 18; // This was observed for currentStatus. It may be 40 for control.
    public static final int CONTROL_MAX_CHUNK_SIZE = 40; // this is a guess, it works for control.BolusPermissionResponse. maybe it should actually be 20?

    private static int determineMaxChunkSize(Message message) {
        if (message.getCharacteristic().equals(Characteristic.CONTROL) && message.type().equals(MessageType.REQUEST)) {
            L.d(TAG, "using maxChunkSize="+CONTROL_MAX_CHUNK_SIZE+" for control request characteristic");
            return CONTROL_MAX_CHUNK_SIZE;
        }

        return DEFAULT_MAX_CHUNK_SIZE;
    }

    public static List<Packet> packetize(Message message, String authenticationKey, byte currentTxId) {
        return packetize(message, authenticationKey, currentTxId, determineMaxChunkSize(message));
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

        L.w(TAG, "packetize signed "+message.signed()+": packetBefore="+ Hex.encodeHexString(packet));
        if (message.signed()) {
            int i = length - 20;
            byte[] bArr2 = new byte[i];
            long pumpStateTimeSinceReset = PumpStateSupplier.pumpTimeSinceReset.get();
            byte[] timeSinceReset = Bytes.toUint32(pumpStateTimeSinceReset);
            L.w(TAG, "using authenticationKey=" + authenticationKey + " pumpTimeSinceReset=" + pumpStateTimeSinceReset);
            System.arraycopy(packet, 0, bArr2, 0, i);
            System.arraycopy(timeSinceReset, 0, bArr2, length - 24, 4);
            byte[] hmacByteKey = authenticationKey.getBytes(Charsets.UTF_8);
            byte[] hmacSha1Output = doHmacSha1(bArr2, hmacByteKey);
            System.arraycopy(bArr2, 0, packet, 0, i);
            System.arraycopy(hmacSha1Output, 0, packet, i, hmacSha1Output.length);
        }
        L.w(TAG, "packetize packetAfter="+ Hex.encodeHexString(packet));

        // Append CRC to packet
        byte[] crc = Bytes.calculateCRC16(packet);
        byte[] packetWithCRC = ArrayUtils.addAll(packet, crc);
        L.w(TAG, "packetize packetWithCRC="+ Hex.encodeHexString(packetWithCRC));

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
