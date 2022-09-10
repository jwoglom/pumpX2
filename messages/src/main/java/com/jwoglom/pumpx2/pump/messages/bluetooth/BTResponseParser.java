package com.jwoglom.pumpx2.pump.messages.bluetooth;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.shared.L;

import com.jwoglom.pumpx2.shared.Hex;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BTResponseParser {
    private static final String TAG = "X2-BTResponseParser";

    public static PumpResponseMessage parse(TronMessageWrapper wrapper, byte[] output, MessageType outputType, UUID uuid) {
        PacketArrayList packetArrayList = wrapper.buildPacketArrayList(outputType);
        return parse(wrapper.message(), packetArrayList, output, uuid);
    }

    public static PumpResponseMessage parse(Message message, PacketArrayList packetArrayList, byte[] output, UUID uuid) {
        L.w(TAG, "Parsing event with: message: "+message+" \npacketArrayList: "+packetArrayList+" \noutput: "+Hex.encodeHexString(output)+" \nuuid: "+uuid.toString());
        checkCharacteristicUuid(uuid, output);

        packetArrayList.validatePacket(output);
        if (!packetArrayList.needsMorePacket()) {
            if (packetArrayList.validate(message.signed() ? PumpStateSupplier.authenticationKey.get() : "")) {
                byte[] a = packetArrayList.messageData();
                byte[] copyOfRange = Arrays.copyOfRange(a, 3, a.length);
                byte b4 = packetArrayList.opCode();
                L.w(TAG, "Parsing message with opcode "+b4);
                Message msg = Messages.parse(copyOfRange, b4, Characteristic.of(uuid));
                L.w(TAG, "Parsed message: " + msg);

                return new PumpResponseMessage(output, msg);
            } else {
                L.w(TAG, "PacketArrayList could not validate");
            }
        } else {
            L.w(TAG, "PacketArrayList needs more packets: "+Hex.encodeHexString(output));
            return new PumpResponseMessage(output);
        }

        return new PumpResponseMessage(output);
    }

    public static Byte parseTxId(byte[] output) {
        Preconditions.checkState(output.length >= 3, "BT-returned data should contain at least 3 bytes: '%s'", Hex.encodeHexString(output));
        /*
        output[0] = packet index mod 15
        output[1] = transaction ID
        output[2] = opcode
         */
        return output[1];
    }

    private static void checkCharacteristicUuid(UUID uuid, byte[] output) {
        // if (Intrinsics.areEqual(uuid, TAllowedCharacteristics.authorizationCharacteristics()) || Intrinsics.areEqual(uuid, TAllowedCharacteristics.currentStatusCharacteristics()) || Intrinsics.areEqual(uuid, TAllowedCharacteristics.controlCharacteristics())) {
        List<UUID> rawBleNotifyUuids = ImmutableList.of(
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
            throw new NotImplementedException("Control stream characteristic: " + Hex.encodeHexString(output));
        } else {
            throw new NotImplementedException("Unsupported UUID: " + uuid + " output: " + Hex.encodeHexString(output));
        }
    }
}
