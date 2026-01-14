package com.jwoglom.pumpx2.cliparser;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.jwoglom.pumpx2.cliparser.util.CharacteristicGuesser;
import com.jwoglom.pumpx2.cliparser.util.JsonMessageParser;
import com.jwoglom.pumpx2.cliparser.util.NoMessageMatch;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.BTResponseParser;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.bluetooth.TronMessageWrapper;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.Packet;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.TimeSinceResetResponse;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.HistoryLog;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.HistoryLogParser;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.DecoderException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jwoglom.pumpx2.shared.Hex;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import com.jwoglom.pumpx2.pump.messages.builders.JpakeAuthBuilder;
import com.jwoglom.pumpx2.pump.messages.builders.crypto.HmacSha256;
import com.jwoglom.pumpx2.pump.messages.builders.crypto.Hkdf;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake1aRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake1bRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake2Request;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake3SessionKeyRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.Jpake4KeyConfirmationRequest;
import io.particle.crypto.EcJpake;

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
        String pumpPairingCode = System.getenv("PUMP_PAIRING_CODE");
        if (!StringUtils.isBlank(pumpPairingCode)) {
            PumpStateSupplier.pumpPairingCode = () -> pumpPairingCode;
        } else if (!StringUtils.isBlank(pumpAuthenticationKey)) {
            PumpStateSupplier.pumpPairingCode = () -> pumpAuthenticationKey;
        } else {
            PumpStateSupplier.pumpPairingCode = () -> "IGNORE_HMAC_SIGNATURE_EXCEPTION";
            System.err.println("Using null pumpPairingCode - signature validation disabled");
        }

        String pumpTimeSinceReset = System.getenv("PUMP_TIME_SINCE_RESET");
        if (!StringUtils.isBlank(pumpTimeSinceReset)){
            PumpStateSupplier.pumpTimeSinceReset = () -> Long.valueOf(pumpTimeSinceReset);
        } else {
            PumpStateSupplier.pumpTimeSinceReset = () -> 0L;
        }
        PumpStateSupplier.actionsAffectingInsulinDeliveryEnabled = () -> true;

        // PacketArrayList.ignoreInvalidTxId = true;

        String filename;
        List<String> lines;
        String[] extra = new String[0];
        if (args.length >= 2) {
            extra = Arrays.copyOfRange(args, 2, args.length);
        }

        String allArgs = Arrays.stream(Arrays.copyOfRange(args, 1, args.length)).collect(Collectors.joining(" "));

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "opcode":
                System.out.println(parseOpcode(args[1]));
                break;
            case "listallcommands":
                listAllCommands();
                break;
            case "parse":
                System.out.println(parseFull(args[1], extra));
                break;
            case "guesscargo":
                System.out.println(parseGuessCargo(args[1], extra));
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
                    String[] parts = line.split("#", 2);
                    String parseLine = parts[0];
                    String commentPart = "";
                    if (parts.length == 2) {
                        commentPart = "\t" + parts[1];
                    }
                    System.out.println(parseLine+commentPart+"\t"+parseFull(parseLine));
                }
                break;
            case "read":
            case "write":
                System.out.println(parse(args[1], extra));
                break;
            case "historylog":
                System.out.println(parseHistoryLog(args[1]));
                break;
            case "bulkhistorylog":
                filename = args[1];
                lines = Files.readAllLines(Path.of(filename));
                for (String line : lines) {
                    System.out.println(parseHistoryLog(line));
                }
                break;
            case "json":
                System.err.println("Waiting for stdin...");
                System.err.flush();
                if (allArgs.isEmpty()) {
                    allArgs = new Scanner(System.in).nextLine();
                }
                System.err.println("Processing "+allArgs);
                System.out.println(JsonMessageParser.parse(allArgs));
                break;
            case "jsonlines":
                filename = args[1];
                lines = Files.readAllLines(Path.of(filename));
                for (String line : lines) {
                    System.err.println("Processing "+line);
                    System.out.println(JsonMessageParser.parse(line));
                }
                break;
            case "encode":
                try {
                    System.out.println(encode(args[1], args[2], args.length > 3 ? args[3] : ""));
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            case "jpake":
                System.out.println(jpakeAuthEncoder(args[1]));
                break;
            case "jpake-server":
                System.out.println(jpakeAuthServer(args[1]));
                break;
            default:
                System.err.println("Nothing to do.");
                break;

        }
    }

    private static String jpakeAuthEncoder(String pairingCode) {
        try {
            JpakeAuthBuilder builder = JpakeAuthBuilder.initializeWithPairingCode(pairingCode);
            Scanner scanner = new Scanner(System.in);

            byte txId = 0;
            
            while (!builder.done() && !builder.invalid()) {
                // Generate next request
                Message request = builder.nextRequest();
                if (request == null) {
                    break;
                }
                
                // Convert request to JSON and output
                System.out.println(builder.getStep().name() + ": " + encode(txId, request, null));
                System.out.flush();
                
                // Increment txId for next request
                txId++;
                
                // Read response hex from stdin
                if (scanner.hasNextLine()) {
                    String responseHex = scanner.nextLine().trim();
                    
                    // Parse the hex string using existing parse method
                    Message response = parse(responseHex);
                    if (response != null) {
                        builder.processResponse(response);
                    } else {
                        JSONObject result = new JSONObject();
                        result.put("error", "Could not parse response message from hex: " + responseHex + " at step " + builder.getStep().name());
                        return result.toString();
                    }
                } else {
                    JSONObject result = new JSONObject();
                    result.put("error", "No response received from stdin. Reached step" + builder.getStep().name());
                    return result.toString();
                }
            }
            
            if (builder.done()) {
                JSONObject result = new JSONObject();
                result.put("derivedSecret", Hex.encodeHexString(builder.getDerivedSecret()));
                result.put("serverNonce", Hex.encodeHexString(builder.getServerNonce()));
                result.put("messageName", "JpakeAuthResult");
                result.put("txId", "" + txId);
                return result.toString();
            } else if (builder.invalid()) {
                JSONObject result = new JSONObject();
                result.put("error", "JPAKE authentication failed - HMAC validation failed at step " + builder.getStep().name());
                return result.toString();
            } else {
                JSONObject result = new JSONObject();
                result.put("error", "JPAKE authentication incomplete at step " + builder.getStep().name());
                return result.toString();
            }
            
        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("error", "Exception during JPAKE authentication: " + e.getMessage());
            return result.toString();
        }
    }

    private static String jpakeAuthServer(String pairingCode) {
        try {
            // Initialize server-side EcJpake with the pairing code
            EcJpake serverJpake = new EcJpake(
                EcJpake.Role.SERVER,
                JpakeAuthBuilder.pairingCodeToBytes(pairingCode)
            );

            Scanner scanner = new Scanner(System.in);
            byte txId = 0;

            // Generate server's round 1 (330 bytes total)
            byte[] serverRound1 = serverJpake.getRound1();

            // Step 1a: Send first 165 bytes
            byte[] serverRound1a = Arrays.copyOfRange(serverRound1, 0, 165);
            JSONObject resp1a = new JSONObject();
            resp1a.put("appInstanceId", 0); // Default app instance ID
            resp1a.put("centralChallengeHash", Hex.encodeHexString(serverRound1a));
            System.out.println("JPAKE_1A: " + encode(String.valueOf((int)txId), "Jpake1aResponse", resp1a.toString()));
            System.out.flush();
            txId++;

            // Read client's round 1a
            if (!scanner.hasNextLine()) {
                return errorJson("No client response for round 1a");
            }
            String clientHex1a = scanner.nextLine().trim();
            Message clientMsg1a = parse(clientHex1a);
            if (!(clientMsg1a instanceof Jpake1aRequest)) {
                return errorJson("Expected Jpake1aRequest, got: " + (clientMsg1a != null ? clientMsg1a.getClass().getName() : "null"));
            }
            byte[] clientRound1a = ((Jpake1aRequest) clientMsg1a).getCentralChallenge();

            // Step 1b: Send next 165 bytes
            byte[] serverRound1b = Arrays.copyOfRange(serverRound1, 165, 330);
            JSONObject resp1b = new JSONObject();
            resp1b.put("appInstanceId", 0); // Default app instance ID
            resp1b.put("centralChallengeHash", Hex.encodeHexString(serverRound1b));
            System.out.println("JPAKE_1B: " + encode(String.valueOf((int)txId), "Jpake1bResponse", resp1b.toString()));
            System.out.flush();
            txId++;

            // Read client's round 1b
            if (!scanner.hasNextLine()) {
                return errorJson("No client response for round 1b");
            }
            String clientHex1b = scanner.nextLine().trim();
            Message clientMsg1b = parse(clientHex1b);
            if (!(clientMsg1b instanceof Jpake1bRequest)) {
                return errorJson("Expected Jpake1bRequest, got: " + (clientMsg1b != null ? clientMsg1b.getClass().getName() : "null"));
            }
            byte[] clientRound1b = ((Jpake1bRequest) clientMsg1b).getCentralChallenge();

            // Combine client's round 1 data and feed to EcJpake
            byte[] clientRound1Full = Bytes.combine(clientRound1a, clientRound1b);
            serverJpake.readRound1(clientRound1Full);

            // Generate and send server's round 2
            byte[] serverRound2 = serverJpake.getRound2();
            JSONObject resp2 = new JSONObject();
            resp2.put("appInstanceId", 0); // Default app instance ID
            resp2.put("centralChallengeHash", Hex.encodeHexString(serverRound2));
            System.out.println("JPAKE_2: " + encode(String.valueOf((int)txId), "Jpake2Response", resp2.toString()));
            System.out.flush();
            txId++;

            // Read client's round 2
            if (!scanner.hasNextLine()) {
                return errorJson("No client response for round 2");
            }
            String clientHex2 = scanner.nextLine().trim();
            Message clientMsg2 = parse(clientHex2);
            if (!(clientMsg2 instanceof Jpake2Request)) {
                return errorJson("Expected Jpake2Request, got: " + (clientMsg2 != null ? clientMsg2.getClass().getName() : "null"));
            }
            byte[] clientRound2 = ((Jpake2Request) clientMsg2).getCentralChallenge();
            serverJpake.readRound2(clientRound2);

            // Derive the shared secret
            byte[] derivedSecret = serverJpake.deriveSecret();

            // Round 3: Session key exchange
            // Read client's session key request
            if (!scanner.hasNextLine()) {
                return errorJson("No client response for round 3");
            }
            String clientHex3 = scanner.nextLine().trim();
            Message clientMsg3 = parse(clientHex3);
            if (!(clientMsg3 instanceof Jpake3SessionKeyRequest)) {
                return errorJson("Expected Jpake3SessionKeyRequest, got: " + (clientMsg3 != null ? clientMsg3.getClass().getName() : "null"));
            }

            // Generate server nonce for round 3
            byte[] serverNonce3 = new byte[8];
            new java.security.SecureRandom().nextBytes(serverNonce3);
            byte[] reserved3 = new byte[8]; // All zeros - must be 8 bytes

            JSONObject resp3 = new JSONObject();
            resp3.put("appInstanceId", 0); // Default app instance ID
            resp3.put("nonce", Hex.encodeHexString(serverNonce3)); // Match constructor parameter name
            resp3.put("reserved", Hex.encodeHexString(reserved3)); // Match constructor parameter name
            System.out.println("JPAKE_3: " + encode(String.valueOf((int)txId), "Jpake3SessionKeyResponse", resp3.toString()));
            System.out.flush();
            txId++;

            // Round 4: Key confirmation
            // Read client's key confirmation
            if (!scanner.hasNextLine()) {
                return errorJson("No client response for round 4");
            }
            String clientHex4 = scanner.nextLine().trim();
            Message clientMsg4 = parse(clientHex4);
            if (!(clientMsg4 instanceof Jpake4KeyConfirmationRequest)) {
                return errorJson("Expected Jpake4KeyConfirmationRequest, got: " + (clientMsg4 != null ? clientMsg4.getClass().getName() : "null"));
            }

            Jpake4KeyConfirmationRequest req4 = (Jpake4KeyConfirmationRequest) clientMsg4;
            byte[] clientNonce4 = req4.getNonce();
            byte[] clientHashDigest = req4.getHashDigest();

            // Validate client's HMAC
            byte[] expectedClientHash = HmacSha256.hmacSha256(
                clientNonce4,
                Hkdf.build(serverNonce3, derivedSecret)
            );

            if (!Arrays.equals(expectedClientHash, clientHashDigest)) {
                return errorJson("Client HMAC validation failed");
            }

            // Generate server's response for round 4
            byte[] serverNonce4 = new byte[8];
            new java.security.SecureRandom().nextBytes(serverNonce4);
            byte[] serverHashDigest = HmacSha256.hmacSha256(
                serverNonce4,
                Hkdf.build(serverNonce3, derivedSecret)
            );

            JSONObject resp4 = new JSONObject();
            resp4.put("appInstanceId", 0); // Default app instance ID
            resp4.put("nonce", Hex.encodeHexString(serverNonce4));
            resp4.put("reserved", Hex.encodeHexString(new byte[8])); // 8 bytes of zeros
            resp4.put("hashDigest", Hex.encodeHexString(serverHashDigest));
            System.out.println("JPAKE_4: " + encode(String.valueOf((int)txId), "Jpake4KeyConfirmationResponse", resp4.toString()));
            System.out.flush();
            txId++;

            // Success! Return the derived secret
            JSONObject result = new JSONObject();
            result.put("derivedSecret", Hex.encodeHexString(derivedSecret));
            result.put("serverNonce", Hex.encodeHexString(serverNonce3));
            result.put("messageName", "JpakeAuthResult");
            result.put("txId", "" + txId);
            return result.toString();

        } catch (Exception e) {
            JSONObject result = new JSONObject();
            result.put("error", "Exception during server JPAKE authentication: " + e.getMessage());
            e.printStackTrace();
            return result.toString();
        }
    }

    private static String errorJson(String message) {
        JSONObject result = new JSONObject();
        result.put("error", message);
        return result.toString();
    }



    private static void assertTrue(String msg, boolean ok) {
        if (!ok) {
            L.w(TAG, "FAIL: " + msg);
        }
    }

    /**
     * Converts a JSON value to the appropriate type for a constructor parameter.
     * Handles primitive types by converting JSON numeric/boolean values appropriately.
     */
    private static Object convertToParameterType(Object value, Class<?> targetType) {
        if (value == null) {
            if (targetType.isPrimitive()) {
                throw new IllegalArgumentException("Cannot pass null for primitive type " + targetType.getName());
            }
            return null;
        }

        // Handle primitive types
        if (targetType == int.class || targetType == Integer.class) {
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            return ((Number) value).intValue();
        } else if (targetType == long.class || targetType == Long.class) {
            if (value instanceof String) {
                return Long.parseLong((String) value);
            }
            return ((Number) value).longValue();
        } else if (targetType == short.class || targetType == Short.class) {
            if (value instanceof String) {
                return Short.parseShort((String) value);
            }
            return ((Number) value).shortValue();
        } else if (targetType == byte.class || targetType == Byte.class) {
            if (value instanceof String) {
                return Byte.parseByte((String) value);
            }
            return ((Number) value).byteValue();
        } else if (targetType == double.class || targetType == Double.class) {
            if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
            return ((Number) value).doubleValue();
        } else if (targetType == float.class || targetType == Float.class) {
            if (value instanceof String) {
                return Float.parseFloat((String) value);
            }
            return ((Number) value).floatValue();
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            return (Boolean) value;
        } else if (targetType == char.class || targetType == Character.class) {
            String s = value.toString();
            return s.isEmpty() ? '\0' : s.charAt(0);
        } else if (targetType == String.class) {
            return value.toString();
        } else if (targetType == byte[].class) {
            // Handle byte array - if value is a String, assume it's hex and decode it
            if (value instanceof String) {
                try {
                    return Hex.decodeHex((String) value);
                } catch (DecoderException e) {
                    throw new IllegalArgumentException("Cannot decode hex string to byte array: " + value, e);
                }
            } else if (value instanceof byte[]) {
                return value;
            } else {
                throw new IllegalArgumentException("Cannot convert " + value.getClass().getName() + " to byte[]");
            }
        } else {
            // For other types, try direct cast
            return targetType.cast(value);
        }
    }

    /**
     * Extracts JSON object keys in their original order from the raw JSON string.
     * JSONObject uses HashMap which doesn't preserve insertion order, so we parse
     * the original string to get the key order.
     */
    private static List<String> extractJsonKeysInOrder(String jsonString) {
        List<String> keys = new ArrayList<>();
        // Match quoted strings that are followed by a colon (JSON object keys)
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"([^\"]+)\"\\s*:");
        java.util.regex.Matcher matcher = pattern.matcher(jsonString);
        while (matcher.find()) {
            keys.add(matcher.group(1));
        }
        return keys;
    }

    public static String encode(String txId, String messageName, String messageParamsJson) throws InstantiationException, IllegalAccessException, NumberFormatException, IllegalArgumentException, InvocationTargetException {
        if (messageParamsJson == null || messageParamsJson.isEmpty()) {
            messageParamsJson = "{}";
        }
        
        // Determine if input is array (positional) or object (named)
        String trimmed = messageParamsJson.trim();
        boolean isArray = trimmed.startsWith("[");
        
        JSONArray paramsArray = null;
        JSONObject paramsObject = null;
        List<String> orderedKeys = null;
        int paramCount;
        
        if (isArray) {
            paramsArray = new JSONArray(messageParamsJson);
            paramCount = paramsArray.length();
        } else {
            paramsObject = new JSONObject(messageParamsJson);
            orderedKeys = extractJsonKeysInOrder(messageParamsJson);
            paramCount = paramsObject.length();
        }
        
        Class<? extends Message> messageClass = null;
        for (Messages m : Messages.values()) {
            if (m.name().equals(messageName)) {
                messageClass = m.requestClass();
                break;
            } else if (m.requestClass().getSimpleName().equals(messageName)) {
                messageClass = m.requestClass();
                break;
            } else if (m.responseClass().getSimpleName().equals(messageName)) {
                messageClass = m.responseClass();
                break;
            }
        }
        if (messageClass == null) {
            System.err.println("Unknown message name: " + messageName);
            System.exit(1);
        }
        Message message = null;
        Object[] params = null;
        for (Constructor<?> constructor : messageClass.getConstructors()) {
            if (constructor.getParameterCount() == paramCount) {
                if (constructor.getParameterCount() == 0) {
                    message = (Message) constructor.newInstance();
                    message.fillWithEmptyCargo();
                    params = new Object[0];
                    break;
                }
                params = new Object[constructor.getParameterCount()];
                Parameter[] parameters = constructor.getParameters();
                
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    Object value = null;
                    
                    if (isArray) {
                        // Array format: positional arguments [val1, val2, ...]
                        value = paramsArray.get(i);
                    } else {
                        // Object format: try named match first, fall back to ordered keys
                        if (paramsObject.has(parameter.getName())) {
                            value = paramsObject.get(parameter.getName());
                        } else if (i < orderedKeys.size()) {
                            // Use the key order extracted from the original JSON string
                            value = paramsObject.get(orderedKeys.get(i));
                        }
                    }
                    
                    params[i] = convertToParameterType(value, parameter.getType());
                }
                message = (Message) constructor.newInstance(params);
                break;
            }
        }
        if (message == null) {
            System.err.println("Unable to build message " + messageName + " with params " + (isArray ? paramsArray : paramsObject) + ": no constructor was found with " + paramCount + " parameters");
            System.exit(1);
        }
        byte currentTxId = (byte) Integer.valueOf(txId).byteValue();
        return encode(currentTxId, message, params);
    }

    private static String encode(byte currentTxId, Message message, @Nullable Object[] params) {
        TronMessageWrapper wrapper;
        String maxChunkSize = System.getenv("PUMPX2_MAX_CHUNK_SIZE");
        if (maxChunkSize == null || maxChunkSize.isEmpty()) {
            wrapper = new TronMessageWrapper(message, currentTxId);
        } else {
            wrapper = new TronMessageWrapper(message, currentTxId, Integer.parseInt(maxChunkSize));
        }

        JSONObject ret = new JSONObject();
        ArrayList<String> packets = new ArrayList<>();
        for (Packet packet : wrapper.packets()) {
            packets.add(Hex.encodeHexString(packet.build()));
        }
        ret.put("txId", "" + currentTxId);
        ret.put("messageName", message.getClass().getSimpleName());
        if (params != null) {
            ret.put("messageParams", params);
        }
        ret.put("packets", packets);
        ret.put("characteristicName", CharacteristicUUID.which(message.getCharacteristic().getUuid()));
        ret.put("characteristic", message.getCharacteristic().getUuid().toString());
        return ret.toString();
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
        Set<Message> messages = new HashSet<>();
        String messageStr = "";
        Exception exc = null;
        Set<Characteristic> possibilities = Messages.findPossibleCharacteristicsForOpcode(opCode);
        //possibilities = CharacteristicGuesser.filterKnownPossibilities(rawHex, opCode, possibilities);
        if (possibilities.size() == 0) {
            return opCode+"\tUnknown opcode "+opCode;
        }
        Characteristic characteristic = null;
        for (Characteristic c : possibilities) {
            try {
                Message message = Messages.fromOpcode(opCode, c).newInstance();
                message.fillWithEmptyCargo();
                messages.add(message);

                // Auto-update pumpTimeSinceReset
                if (message instanceof TimeSinceResetResponse) {
                    PumpStateSupplier.pumpTimeSinceReset = ((TimeSinceResetResponse) message)::getCurrentTime;
                }

                if (messageStr.length() > 0) {
                    messageStr += " <OR> ";
                }
                messageStr += message.getClass().getName().replace("com.jwoglom.pumpx2.pump.messages.", "");
                return opCode+"\t"+messageStr;
            } catch (InstantiationException|IllegalAccessException|IllegalArgumentException|NullPointerException e) {
                e.printStackTrace();
                exc = e;
            }
        }
        throw new RuntimeException(exc);
    }

    public static String parseFull(String rawHex, String ...extra) throws DecoderException {
        String ret = parseOpcode(rawHex);
        System.err.println("parse with "+rawHex+" "+ String.join(",", extra));
        try {
            Message message = parse(rawHex, extra);
            return ret+"\t"+message.toString();
        } catch (Exception e) {
            return ret+"\t"+e.toString();
        }
    }

    public static String parseGuessCargo(String rawHex, String ...extra) throws DecoderException {
        String ret = parseOpcode(rawHex);
        System.err.println("parse with "+rawHex+" "+ String.join(",", extra));
        try {
            Message message = parse(rawHex, extra);
            return guessCargo(message);
        } catch (Exception e) {
            return ret+"\t"+e.toString();
        }
    }

    public static String guessCargo(Message message) {
        byte[] cargo = message.getCargo();
        int max = message.props().size();
        String ret = "";
        for (int i=0; i<max; i++) {
            ret += (i+" "+cargo[i]+"\t");
            if (i+4 <= max) {ret += ("uint32: "+ Bytes.readUint32(cargo, i)+"\t");}
            if (i+2 <= max) {ret += ("short: "+Bytes.readShort(cargo,i)+"\t");}
            if (i+4 <= max) {ret += ("float: "+Bytes.readFloat(cargo, i)+"\t");}
            ret += "\n";
        }
        return ret;
    }


    @SuppressWarnings("deprecation")
    public static Message parse(String rawHex, String ...extra) throws DecoderException {
        List<String> packets = splitRawHexPackets(rawHex, extra);
        String firstRawHex = packets.remove(0);
        String[] remainingRawHex = packets.toArray(new String[0]);
        byte[] initialRead = Hex.decodeHex(firstRawHex);
        int txId = initialRead[1];
        int opCode = initialRead[2];
        Message message = null;
        Characteristic characteristic = null;
        try {
            Set<Characteristic> possibilities = Messages.findPossibleCharacteristicsForOpcode(opCode);
            if (possibilities.size() > 1) {
                System.err.print("Multiple characteristics possible for opCode: "+opCode+": "+possibilities+" ");
                possibilities = CharacteristicGuesser.filterKnownPossibilities(rawHex, opCode, possibilities);
                if (possibilities.contains(Characteristic.CONTROL)) {
                    System.err.print("Using CONTROL");
                    possibilities = Set.of(Characteristic.CONTROL);
                } else if (possibilities.contains(Characteristic.AUTHORIZATION)) {
                    System.err.print("Using AUTHORIZATION");
                    possibilities = Set.of(Characteristic.AUTHORIZATION);
                } else if (possibilities.contains(Characteristic.CURRENT_STATUS)) {
                    System.err.print("Using CURRENT_STATUS");
                    possibilities = Set.of(Characteristic.CURRENT_STATUS);
                } else {
                    return null;
                }
                System.out.print("\n");
            }
            if (possibilities.size() == 0) {
                System.err.print("Unknown opcode "+opCode);
                return null;
            }
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
            return testRequest(firstRawHex, txId, characteristic.getUuid(), message, remainingRawHex);
        } catch (IllegalStateException e) {
            System.err.print("Needs more packets"); // for "+opCode+": "+e.getMessage());
            return null;
        }
    }

    public static Message testRequest(String rawHex, int txId, @Nullable UUID uuid, Message expected, String ...extraRawHex) throws DecoderException {
        List<String> packets = splitRawHexPackets(rawHex, extraRawHex);
        String firstRawHex = packets.remove(0);
        String[] remainingRawHex = packets.toArray(new String[0]);
        byte[] initialRead = Hex.decodeHex(firstRawHex);

        if (uuid == null) {
            uuid = CharacteristicUUID.determine(expected);
        }
        System.err.println("testRequest uuid="+Characteristic.of(uuid));
        MessageType messageType = expected.type();

        TronMessageWrapper tron = new TronMessageWrapper(expected, (byte) txId);
        PacketArrayList packetArrayList = tron.buildPacketArrayList(messageType);
        PumpResponseMessage resp = BTResponseParser.parse(tron.message(), packetArrayList, initialRead, uuid);

        for (String exRawHex : remainingRawHex) {
            System.err.print("extraRawHex: "+exRawHex);
            byte[] nextRead = Hex.decodeHex(exRawHex);
            resp = BTResponseParser.parse(tron.message(), packetArrayList, nextRead, uuid);
        }

        assertTrue("Response message returned from parser: " + resp, resp.message().isPresent());

        if (!resp.message().isPresent()) {
            System.err.print("Message not fully formed");
            return null;
        }
        Message parsedMessage = resp.message().get();
        L.d(TAG, String.format("Parsed: %s\nExpected: %s", parsedMessage, expected));
        assertTrue("classes match", expected.getClass().equals(parsedMessage.getClass()));

        int numPackets = tron.packets().size();
        byte[] mergedPackets = tron.mergeIntoSinglePacket().build();

        if (parsedMessage instanceof TimeSinceResetResponse) {
            TimeSinceResetResponse tsr = (TimeSinceResetResponse) parsedMessage;
            PumpStateSupplier.pumpTimeSinceReset = tsr::getPumpTimeSecondsSinceReset;
        }

        return parsedMessage;
    }

    private static List<String> splitRawHexPackets(String rawHex, String ...extraRawHex) {
        List<String> packets = new ArrayList<>();
        for (String entry : concatRawHexInputs(rawHex, extraRawHex)) {
            for (String token : entry.split("[,\\s]+")) {
                if (!token.isEmpty()) {
                    packets.add(token);
                }
            }
        }
        return packets;
    }

    private static List<String> concatRawHexInputs(String rawHex, String ...extraRawHex) {
        List<String> packets = new ArrayList<>();
        packets.add(rawHex);
        packets.addAll(Arrays.asList(extraRawHex));
        return packets;
    }

    public static String parseHistoryLog(String rawHex) throws DecoderException {
        HistoryLog message = HistoryLogParser.parse(Hex.decodeHex(rawHex));
        String type = message.getClass().getName().replace("com.jwoglom.pumpx2.pump.messages.", "");
        int typeId = message.typeId();
        return typeId+"\t"+type+"\t"+rawHex+"\t"+message;
    }

    public static void listAllCommands() {
        Map<Integer, String> currentStatus = new HashMap<>();
        Map<Integer, String> control = new HashMap<>();
        for (Messages m : Messages.values()) {
            if (m.requestProps().characteristic().equals(Characteristic.CURRENT_STATUS)) {
                currentStatus.put(sanitizedOpcode(m.requestOpCode()), m.requestClass().getSimpleName());
            } else if (m.requestProps().characteristic().equals(Characteristic.CONTROL)) {
                control.put(sanitizedOpcode(m.requestOpCode()), m.requestClass().getSimpleName());
            }
        }

        System.out.println("CurrentStatus:");
        for (Integer opCode : currentStatus.keySet().stream().sorted().collect(Collectors.toList())) {
            System.out.println(opCode+"\t"+currentStatus.get(opCode));
        }
        System.out.println("\n");
        System.out.println("Control:");
        for (Integer opCode : control.keySet().stream().sorted().collect(Collectors.toList())) {
            System.out.println(opCode+"\t"+control.get(opCode));
        }
    }

    public static Integer sanitizedOpcode(int opCode) {
        return (int) ((byte) opCode);
    }
}
