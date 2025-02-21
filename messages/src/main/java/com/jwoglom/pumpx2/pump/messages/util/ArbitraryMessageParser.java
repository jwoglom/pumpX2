package com.jwoglom.pumpx2.pump.messages.util;

import org.apache.commons.lang3.StringUtils;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;

import org.apache.commons.codec.DecoderException;
import com.jwoglom.pumpx2.shared.Hex;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Helper class which parses arbitrary messages (with or without the known opcode and transaction ID)
 */
public class ArbitraryMessageParser {

    public static void setPumpTimeSinceReset(String pumpTimeSinceReset) {
        if (!StringUtils.isBlank(pumpTimeSinceReset)){
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
            Set<Characteristic> possibilities = Messages.findPossibleCharacteristicsForOpcode(opCode);
            if (possibilities.size() > 1) {
                System.err.print("Multiple characteristics possible for opCode: "+opCode+": "+possibilities);
                return Optional.empty();
            }
            message = Messages.fromOpcode(opCode, possibilities.stream().iterator().next()).newInstance();
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
        PumpResponseMessage resp = BTResponseParser.parse(tron, initialRead, messageType, uuid);
        return resp.message();
    }
}
