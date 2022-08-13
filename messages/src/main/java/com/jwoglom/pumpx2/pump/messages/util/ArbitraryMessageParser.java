package com.jwoglom.pumpx2.pump.messages.util;

import com.google.common.base.Strings;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessageEvent;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.util.Optional;
import java.util.UUID;

/**
 * Helper class which parses arbitrary messages (with or without the known opcode and transaction ID)
 */
public class ArbitraryMessageParser {
    public static void setPumpAuthenticationKey(String pumpAuthenticationKey) {
        if (!Strings.isNullOrEmpty(pumpAuthenticationKey)) {
            PumpStateSupplier.authenticationKey = () -> pumpAuthenticationKey;
        }
    }

    public static void setPumpTimeSinceReset(String pumpTimeSinceReset) {
        if (!Strings.isNullOrEmpty(pumpTimeSinceReset)){
            PumpStateSupplier.pumpTimeSinceReset = () -> Long.valueOf(pumpTimeSinceReset);
        }
    }

    @SuppressWarnings("deprecation")
    public static Optional<Message> parse(String rawHex) throws DecoderException {
        byte[] initialRead = Hex.decodeHex(rawHex);
        int txId = initialRead[1];
        int opCode = initialRead[2];
        Message message = null;
        try {
            message = Messages.fromOpcode(opCode).newInstance();
            message.fillWithEmptyCargo();
        } catch (NullPointerException e) {
            System.err.print("Unknown opcode "+opCode);
            return Optional.empty();
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            return testRequest(rawHex, txId, message);
        } catch (IllegalStateException e) {
            System.err.print("Needs more packets"); // for "+opCode+": "+e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<Message> testRequest(String rawHex, int txId, Message expected) throws DecoderException {
        byte[] initialRead = Hex.decodeHex(rawHex);

        UUID uuid = CharacteristicUUID.determine(expected);
        MessageType messageType = expected.type();

        TronMessageWrapper tron = new TronMessageWrapper(expected, (byte) txId);
        PumpResponseMessageEvent resp = BTResponseParser.parse(tron, initialRead, messageType, uuid);
        return resp.message();
    }
}
