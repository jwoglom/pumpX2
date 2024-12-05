package com.jwoglom.pumpx2.cliparser.util;

import com.google.common.base.Strings;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.pump.messages.models.UnexpectedTransactionIdException;
import com.jwoglom.pumpx2.shared.Hex;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.DecoderException;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PumpResponseMessageBuilder {
    private static final String TAG = "PumpResponseMessageBuilder";


    public static PumpResponseMessage build(String valueStr, String btChar, String... extraValueStr) throws NoMessageMatch {
        PumpResponseMessage resp = makeMessage(valueStr, btChar, extraValueStr);
        if ((resp == null || !resp.message().isPresent()) && (extraValueStr == null || extraValueStr.length == 0)) {
            L.d(TAG, "Message not fully formed");

            // Hack to split messages which contain two packets into individual packets for parsing
            if (!valueStr.substring(0, 2).equals("00")) {
                String op = valueStr.substring(2, 4);
                int num = Integer.parseInt(valueStr.substring(0, 2), 16);
                List<String> messages = new ArrayList<>();
                String remainder = valueStr;
                L.d(TAG, "rawHex: "+remainder);
                for (int i=num-1; i>=0; i--) {
                    L.d(TAG, "i="+i+" num="+num);
                    String spl = Integer.toHexString(i) + op;
                    if (spl.length() == 3) {
                        spl = "0"+spl;
                    }
                    L.d(TAG, "spl:"+spl);
                    String[] split = remainder.split(spl);
                    messages.add(split[0]);
                    L.d(TAG, "messages:"+messages);
                    if (split.length > 1) {
                        remainder = spl + split[1];
                    } else {
                        remainder = "";
                    }
                    L.d(TAG, "remainder: "+remainder);
                }
                if (!remainder.isEmpty()) {
                    messages.add(remainder);
                }
                String firstRawHex = messages.remove(0);
                String[] newExtra = messages.toArray(new String[0]);
                return makeMessage(firstRawHex, btChar, newExtra);
            }
            return null;
        }

        return resp;
    }


    private static PumpResponseMessage makeMessage(String valueStr, String btChar, String... extraValueStr) throws NoMessageMatch {
        byte[] value;
        try {
            value = Hex.decodeHex(valueStr);
        } catch (JSONException | DecoderException e) {
            L.d(TAG, e.getMessage());
            return null;
        }

        if (Strings.isNullOrEmpty(btChar)) {
            Characteristic guessedCharacteristic = CharacteristicGuesser.guessBestCharacteristic(valueStr, value[2]);
            L.i(TAG, "guessed characteristic for opCode "+value[2]+": "+guessedCharacteristic);
            btChar = guessedCharacteristic.getUuid().toString().replace("-", "");
        }

        UUID uuid = btCharToUuid(btChar);
        if (uuid == null) {
            if (btChar.length() == 32) {
                UUID withDashes = addDashesToUuid(btChar);
                String which = CharacteristicUUID.which(withDashes);
                if (!Strings.isNullOrEmpty(which)) {
                    throw new NoMessageMatch.NonPumpBtMessage(withDashes, which, valueStr);
                }
            }
            throw new NoMessageMatch.NonPumpBtMessage(null, null, valueStr);
        }
        Characteristic characteristic = Characteristic.of(uuid);
        if (characteristic == null) {
            throw new NoMessageMatch.NonPumpBtMessage(uuid, CharacteristicUUID.which(uuid), valueStr);
        }

        int opCode = value[2];
        byte txId = value[3];
        L.d(TAG, "makeMessage(txId="+txId+", opCode="+opCode+")");

        if (Strings.isNullOrEmpty(btChar)) {
            throw new RuntimeException("missing characteristic, cannot parse");
        }

        Message message = null;
        try {
            Class messageClass = Messages.fromOpcode(opCode, characteristic);
            if (messageClass == null) {
                UUID withDashes = addDashesToUuid(btChar);
                String which = CharacteristicUUID.which(withDashes);

                List<String> knownMessages = Messages.findPossibleMessagesForCharacteristic(characteristic).stream().map(c -> {
                    try {
                        Message m = c.newInstance();
                        return m.messageName()+" (" + m.opCode() + ")";
                    } catch (InstantiationException|IllegalAccessException e) {
                        return e.toString();
                    }
                }).collect(Collectors.toList());
                throw new NoMessageMatch.NoOpCodeForCharacteristicException((byte) opCode, withDashes, which, valueStr, knownMessages);
            }
            message = (Message) messageClass.newInstance();
            message.fillWithEmptyCargo();
        } catch (InstantiationException | IllegalAccessException e) {
            L.d(TAG, e.getMessage());
            return null;
        }

        try {
            return makeResponseMessage(message, value, (byte) txId, characteristic, extraValueStr);
        } catch (UnexpectedTransactionIdException e) {
            L.e(TAG, "UnexpectedTransactionIdException(foundTxId="+e.foundTxId+", txId="+txId+")");
            if ((byte) e.foundTxId != (byte) txId) {
                return makeResponseMessage(message, value, (byte) e.foundTxId, characteristic, extraValueStr);
            } else {
                throw e;
            }
        }
    }


    private static PumpResponseMessage makeResponseMessage(Message message, byte[] value, byte txId, Characteristic characteristic, String... extraValueStr) {
        TronMessageWrapper tron = new TronMessageWrapper(message, (byte) txId);
        PacketArrayList packetArrayList = tron.buildPacketArrayList(message.type());
        PumpResponseMessage resp;

        resp = BTResponseParser.parse(tron.message(), packetArrayList, value, characteristic.getUuid());

        try {
            if (extraValueStr != null) {
                for (String exRawHex : extraValueStr) {
                    L.d(TAG, "extraValueStr: " + exRawHex);
                    byte[] nextRead = Hex.decodeHex(exRawHex);
                    resp = BTResponseParser.parse(tron.message(), packetArrayList, nextRead, characteristic.getUuid());
                }
            }
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }

        return resp;
    }

    private static UUID btCharToUuid(String btChar) {
        for (Characteristic c : Characteristic.values()) {
            String cChar = c.getUuid().toString().replace("-", "");
            if (cChar.toLowerCase().equals(btChar.toLowerCase())) {
                return c.getUuid();
            }
        }
        L.e(TAG, "No characteristic found for UUID "+btChar);
        return null;
    }

    private static UUID addDashesToUuid(String btChar) {
        return UUID.fromString(btChar.substring(0, 8) + "-" +
                btChar.substring(8, 12) + "-" +
                btChar.substring(12, 16) + "-" +
                btChar.substring(16, 20) + "-" +
                btChar.substring(20));
    }

}
