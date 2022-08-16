package com.jwoglom.pumpx2.cliparser;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class Main {
    private static final String TAG = "CLIParser";
    static {
        L.getPrintln = System.err::println;
    }
    public static void main(String[] args) throws DecoderException, IOException {
        if (args.length == 0) {
            System.out.println("Specify a command.");
            return;
        }

        String pumpAuthenticationKey = System.getenv("PUMP_AUTHENTICATION_KEY");
        if (!Strings.isNullOrEmpty(pumpAuthenticationKey)) {
            PumpStateSupplier.authenticationKey = () -> pumpAuthenticationKey;
        }

        String pumpTimeSinceReset = System.getenv("PUMP_TIME_SINCE_RESET");
        if (!Strings.isNullOrEmpty(pumpTimeSinceReset)){
            PumpStateSupplier.pumpTimeSinceReset = () -> Long.valueOf(pumpTimeSinceReset);
        }

        String filename;
        List<String> lines;
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "opcode":
                System.out.println(parseOpcode(args[1]));
                break;
            case "parse":
                System.out.println(parseFull(args[1]));
                break;
            case "bulkopcode":
                filename = args[1];
                lines = Files.readAllLines(Path.of(filename));
                for (String line : lines) {
                    System.out.println(line+"\t"+parseOpcode(line));
                }
                break;
            case "bulkparse":
                filename = args[1];
                lines = Files.readAllLines(Path.of(filename));
                for (String line : lines) {
                    System.out.println(line+"\t"+parseFull(line));
                }
                break;
            case "read":
            case "write":
                System.out.println(parse(args[1]));
                break;
        }
    }

    private static void assertTrue(String msg, boolean ok) {
        if (!ok) {
            L.w(TAG, "FAIL: " + msg);
        }
    }

    public static String parseOpcode(String rawHex) throws DecoderException {
        byte[] initialRead;
        try {
            initialRead = Hex.decodeHex(rawHex);
        } catch (Exception e) {
            return "Unable to parse: " + e;
        }
        int txId = initialRead[1];
        int opCode = initialRead[2];
        Message message = null;
        try {
            Set<Characteristic> possibilities = Messages.findPossibleCharacteristicsForOpcode(opCode);            if (possibilities.size() > 1) {
                System.err.print("Multiple characteristics possible for opCode: "+opCode+": "+possibilities);
                if (possibilities.contains(Characteristic.CONTROL)) {
                    System.err.print("Using CONTROL");
                    possibilities = ImmutableSet.of(Characteristic.CONTROL);
                } else if (possibilities.contains(Characteristic.CURRENT_STATUS)) {
                    System.err.print("Using CURRENT_STATUS");
                    possibilities = ImmutableSet.of(Characteristic.CURRENT_STATUS);
                } else {
                    return "Multiple possibilities "+opCode+" "+possibilities;
                }
            }
            if (possibilities.size() == 0) {
                return "Unknown opcode "+opCode;
            }
            Characteristic characteristic = null;
            for (Characteristic c : possibilities) {
                characteristic = c;
            }
            message = Messages.fromOpcode(opCode, characteristic).newInstance();
            message.fillWithEmptyCargo();
        } catch (NullPointerException e) {
            return "Unknown opcode "+opCode;
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }

        return opCode+"\t"+message.getClass().getName().replace("com.jwoglom.pumpx2.pump.messages.", "");
    }

    public static String parseFull(String rawHex) throws DecoderException {
        String ret = parseOpcode(rawHex);
        try {
            Message message = parse(rawHex);
            return ret+"\t"+message.toString();
        } catch (Exception e) {
            return ret+"\t"+e.toString();
        }
    }

    @SuppressWarnings("deprecation")
    public static Message parse(String rawHex) throws DecoderException {
        byte[] initialRead = Hex.decodeHex(rawHex);
        int txId = initialRead[1];
        int opCode = initialRead[2];
        Message message = null;
        try {
            Set<Characteristic> possibilities = Messages.findPossibleCharacteristicsForOpcode(opCode);
            if (possibilities.size() > 1) {
                System.err.print("Multiple characteristics possible for opCode: "+opCode+": "+possibilities);
                if (possibilities.contains(Characteristic.CONTROL)) {
                    System.err.print("Using CONTROL");
                    possibilities = ImmutableSet.of(Characteristic.CONTROL);
                } else if (possibilities.contains(Characteristic.CURRENT_STATUS)) {
                    System.err.print("Using CURRENT_STATUS");
                    possibilities = ImmutableSet.of(Characteristic.CURRENT_STATUS);
                } else {
                    return null;
                }
            }
            if (possibilities.size() == 0) {
                System.err.print("Unknown opcode "+opCode);
                return null;
            }
            Characteristic characteristic = null;
            for (Characteristic c : possibilities) {
                characteristic = c;
            }
            message = Messages.fromOpcode(opCode, characteristic).newInstance();
            message.fillWithEmptyCargo();
        } catch (NullPointerException e) {
            System.err.print("NPE for opcode "+opCode);
            return null;
        } catch (InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            return testRequest(rawHex, txId, message);
        } catch (IllegalStateException e) {
            System.err.print("Needs more packets"); // for "+opCode+": "+e.getMessage());
            return null;
        }
    }

    public static Message testRequest(String rawHex, int txId, Message expected) throws DecoderException {
        byte[] initialRead = Hex.decodeHex(rawHex);

        UUID uuid = CharacteristicUUID.determine(expected);
        MessageType messageType = expected.type();

        TronMessageWrapper tron = new TronMessageWrapper(expected, (byte) txId);
        PumpResponseMessage resp = BTResponseParser.parse(tron, initialRead, messageType, uuid);
        assertTrue("Response message returned from parser: " + resp, resp.message().isPresent());

        if (!resp.message().isPresent()) {
            System.err.print("Message not fully formed");
            return null;
        }
        Message parsedMessage = resp.message().get();
        L.w(TAG, String.format("Parsed: %s\nExpected: %s", parsedMessage, expected));
        assertTrue("classes match", expected.getClass().equals(parsedMessage.getClass()));

        int numPackets = tron.packets().size();
        byte[] mergedPackets = tron.mergeIntoSinglePacket().build();

        return parsedMessage;
    }
}