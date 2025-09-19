package com.jwoglom.pumpx2.pump.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.shared.L;
import com.jwoglom.pumpx2.shared.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Packetize {
    private static final Logger log = LoggerFactory.getLogger(Packetize.class);
    public static TransactionId txId = new TransactionId();

    // TODO: these comments about chunk size are almost certainly due to the MTU.
    public static final int DEFAULT_MAX_CHUNK_SIZE = 18; // This was observed for currentStatus. It may be 40 for control.
    public static final int CONTROL_MAX_CHUNK_SIZE = 40; // this is a guess, it works for control.BolusPermissionResponse. maybe it should actually be 20?

    private static int determineMaxChunkSize(Message message) {
        if (message.getCharacteristic().equals(Characteristic.CONTROL) && message.type().equals(MessageType.REQUEST)) {
            log.debug("using maxChunkSize="+CONTROL_MAX_CHUNK_SIZE+" for control request characteristic");
            return CONTROL_MAX_CHUNK_SIZE;
        }

        return DEFAULT_MAX_CHUNK_SIZE;
    }

    public static List<Packet> packetize(Message message, byte[] authenticationKey, byte currentTxId) {
        return packetize(message, authenticationKey, currentTxId, determineMaxChunkSize(message));
    }

    public static List<Packet> packetize(Message message, byte[] authenticationKey, byte currentTxId, int maxChunkSize) {
        if (message == null) {
            log.error("packetize has null message");
        } else if (message.getCargo() == null) {
            log.error("packetize has null messagecargo messageName="+message.messageName());
            log.error("packetize has null messagecargo message="+message+" authKey="+Hex.encodeHexString(authenticationKey));
        }
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

        //log.debug("packetize signed "+message.signed()+": packetBefore="+ Hex.encodeHexString(packet));
        if (message.props().modifiesInsulinDelivery() && !PumpStateSupplier.actionsAffectingInsulinDeliveryEnabled.get()) {
            throw new ActionsAffectingInsulinDeliveryNotEnabledInPumpX2Exception();
        }
        if (message.signed()) {
            int i = length - 20;
            byte[] messageData = new byte[i];
            System.arraycopy(packet, 0, messageData, 0, i);
            long pumpStateTimeSinceReset = PumpStateSupplier.pumpTimeSinceReset.get();
            byte[] timeSinceReset = Bytes.toUint32(pumpStateTimeSinceReset);
            System.arraycopy(timeSinceReset, 0, messageData, length - 24, 4);

            log.debug("using authenticationKey=" + Hex.encodeHexString(authenticationKey) + " pumpTimeSinceReset=" + pumpStateTimeSinceReset);

            byte[] hmacSha1Output = doHmacSha1(messageData, authenticationKey);
            System.arraycopy(messageData, 0, packet, 0, i);
            System.arraycopy(hmacSha1Output, 0, packet, i, hmacSha1Output.length);
        }
        //log.debug("packetize packetAfter="+ Hex.encodeHexString(packet));

        // Append CRC to packet
        byte[] crc = Bytes.calculateCRC16(packet);
        byte[] packetWithCRC = ArrayUtils.addAll(packet, crc);
        //log.debug("packetize packetWithCRC="+ Hex.encodeHexString(packetWithCRC));

        // Fill Packet list with chunks of size 18 (maxChunkSize)
        List<Packet> packets = new ArrayList<>();
        List<List<Byte>> chunked = partitionList(packetWithCRC, maxChunkSize);

        int b = chunked.size() - 1;
        for (List<Byte> bytes : chunked) {
            byte[] elem = ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
            packets.add(new Packet((byte) b, currentTxId, elem));
            b--;
        }

        return packets;
    }

    public static List<List<Byte>> partitionList(byte[] packetWithCRC, int partitionSize) {
        List<List<Byte>> partitions = new ArrayList<>();
        List<Byte> subList = new ArrayList<>();

        for (byte b : packetWithCRC) {
            subList.add(b);
            if (subList.size() == partitionSize) {
                partitions.add(subList);
                subList = new ArrayList<>();
            }
        }

        if (!subList.isEmpty()) {
            partitions.add(subList);
        }

        return partitions;
    }

    public static byte[] doHmacSha1(byte[] data, byte[] key) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, key).hmac(data);
    }

    public static class ActionsAffectingInsulinDeliveryNotEnabledInPumpX2Exception extends RuntimeException {
        ActionsAffectingInsulinDeliveryNotEnabledInPumpX2Exception() {
            super("The developer of this application has not enabled actions which affect insulin delivery");
        }
    }
}
