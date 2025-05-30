package com.jwoglom.pumpx2.pump.messages.bluetooth;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;

import com.jwoglom.pumpx2.shared.Hex;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BTResponseParser {
    //private static final String TAG = "BTResponseParser";

    private static final Logger log = LoggerFactory.getLogger(BTResponseParser.class);

    public static PumpResponseMessage parse(TronMessageWrapper wrapper, byte[] output, MessageType outputType, UUID uuid) {
        PacketArrayList packetArrayList = wrapper.buildPacketArrayList(outputType);
        return parse(wrapper.message(), packetArrayList, output, uuid);
    }

    @SuppressWarnings("DefaultLocale")
    public static PumpResponseMessage parse(Message message, PacketArrayList packetArrayList, byte[] output, UUID uuid) {
        log.debug("Parsing event with: message: "+message+" \npacketArrayList: "+packetArrayList+" \noutput: "+Hex.encodeHexString(output)+" \nuuid: "+uuid.toString());
        checkCharacteristicUuid(uuid, output);

        packetArrayList.validatePacket(output);
        if (!packetArrayList.needsMorePacket()) {
            byte[] authKeyVal = new byte[0];
            if (message.signed()) {
                authKeyVal = PumpStateSupplier.authenticationKey.get();
            } else {
                try {
                    authKeyVal = PumpStateSupplier.authenticationKey.get();
                } catch (IllegalStateException ignore) {
                    authKeyVal = new byte[0];
                }
            }
            if (packetArrayList.validate(authKeyVal)) {
                byte[] a = packetArrayList.messageData();
                byte[] copyOfRange = Arrays.copyOfRange(a, 3, a.length);
                byte b4 = packetArrayList.opCode();
                byte txId = packetArrayList.getExpectedTxId();
                log.debug( "Parsing message with opcode "+b4);
                Message msg = Messages.parse(copyOfRange, b4, Characteristic.of(uuid));
                if (msg == null) {
                    log.warn(String.format("PARSED-MESSAGE(txId=%-3d, %s)\tFAILURE: %s, %s: %s", txId, CharacteristicUUID.which(uuid), b4, message.signed(), Hex.encodeHexString(copyOfRange)));
                } else {
                    log.info(String.format("PARSED-MESSAGE(txId=%-3d, %s):\t%s", txId, CharacteristicUUID.which(uuid), msg));
                }

                return new PumpResponseMessage(output, msg);
            } else {
                log.debug( "PacketArrayList could not validate");
            }
        } else {
            log.info( "PacketArrayList needs more packets: "+Hex.encodeHexString(output));
            return new PumpResponseMessage(output);
        }

        return new PumpResponseMessage(output);
    }

    public static PumpResponseMessage parseBestEffortForLogging(byte[] output, UUID uuid) {
        if (output.length < 3) {
            log.error( "parseBestEffortForLogging input has less than 3 bytes");
            return null;
        }
        byte txId = parseTxId(output);
        byte opCode = parseOpcode(output);
        MessageType messageType = MessageType.fromOpcodeBestEffort(opCode);

        Message message = null;
        try {
            message = Messages.fromOpcode(opCode, Characteristic.of(uuid)).newInstance();
            message.fillWithEmptyCargo();
        } catch (NullPointerException|InstantiationException|IllegalAccessException e) {
            log.error( "parseBestEffortForLogging", e);
            return null;
        }

        try {
            TronMessageWrapper tron = new TronMessageWrapper(message, (byte) txId);
            PacketArrayList packetArrayList = tron.buildPacketArrayList(messageType);
            return BTResponseParser.parse(tron.message(), packetArrayList, output, uuid);
        } catch (Exception e) {
            log.error( "parseBestEffortForLogging", e);
            return null;
        }
    }

    public static Byte parseTxId(byte[] output) {
        Validate.isTrue(output.length >= 3, "BT-returned data should contain at least 3 bytes: '%s'", Hex.encodeHexString(output));
        /*
        output[0] = packet index mod 15
        output[1] = transaction ID
        output[2] = opcode
         */
        return output[1];
    }

    public static Byte parseOpcode(byte[] output) {
        Validate.isTrue(output.length >= 3, "BT-returned data should contain at least 3 bytes: '%s'", Hex.encodeHexString(output));
        /*
        output[0] = packet index mod 15
        output[1] = transaction ID
        output[2] = opcode
         */
        return output[2];
    }

    private static void checkCharacteristicUuid(UUID uuid, byte[] output) {
        // if (Intrinsics.areEqual(uuid, TAllowedCharacteristics.authorizationCharacteristics()) || Intrinsics.areEqual(uuid, TAllowedCharacteristics.currentStatusCharacteristics()) || Intrinsics.areEqual(uuid, TAllowedCharacteristics.controlCharacteristics())) {
        List<UUID> rawBleNotifyUuids = Arrays.asList(
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                CharacteristicUUID.CONTROL_CHARACTERISTICS);

        if (rawBleNotifyUuids.contains(uuid)) {
            // normal
        } else if (CharacteristicUUID.QUALIFYING_EVENTS_CHARACTERISTICS.equals(uuid)) {
            // Qualifying event of:
            // bluetoothGattCharacteristic.getIntValue(20, 0).intValue()
            throw new NotImplementedException("Qualifying event characteristic: " + Hex.encodeHexString(output));
        } else if (CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS.equals(uuid)) {
            // stream write
            //throw new NotImplementedException("History log characteristic: " + Hex.encodeHexString(output));
        } else if (CharacteristicUUID.SERVICE_CHANGED_CHARACTERISTICS.equals(uuid)) {
            // onServiceCharacteristicsChanged
            throw new NotImplementedException("Service changed characteristic: " + Hex.encodeHexString(output));
        } else if (CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS.equals(uuid)) {
            // Control stream (opcodes: -27, -25, -23)
            //throw new NotImplementedException("Control stream characteristic: " + Hex.encodeHexString(output));
        } else {
            throw new NotImplementedException("Unsupported UUID: " + uuid + " output: " + Hex.encodeHexString(output));
        }
    }
}
