package com.jwoglom.pumpx2.cliparser.util;

import com.google.common.base.Strings;
import com.jwoglom.pumpx2.cliparser.util.PumpResponseMessageBuilder;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier;
import com.jwoglom.pumpx2.pump.messages.bluetooth.models.PumpResponseMessage;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.TimeSinceResetResponse;
import com.jwoglom.pumpx2.shared.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonMessageParser {
    private static final String TAG = "JsonMessageParser";


    public static String parse(String line) {
        try {
            return parseInternalNoErrorHandling(line);
        } catch (NoMessageMatch e) {
            JSONObject o = new JSONObject();
            JSONObject o2 = new JSONObject();
            o2.put("message", e.getMessage());
            o2.put("class", e.getClass().getName());
            o2.put("uuid", e.uuid);
            o2.put("uuidName", e.uuidName);
            o2.put("value", e.value);
            if (e instanceof NoMessageMatch.NoOpCodeForCharacteristicException) {
                o2.put("opCode", ((NoMessageMatch.NoOpCodeForCharacteristicException)e).opCode);
            }
            o2.putOpt("debugMsg", e.debugMsg);
            o.put("error", o2);
            try {
                o.put("raw", new JSONObject(line));
            } catch (Exception e2) {
                o.put("raw", line);
            }
            return o.toString(0);
        } catch (Exception e) {
            JSONObject o = new JSONObject();
            JSONObject o2 = new JSONObject();
            o2.put("message", e.getMessage());
            o2.put("class", e.getClass().getName());
            o2.put("stacktrace", e.getStackTrace());
            o.put("error", o2);
            try {
                o.put("raw", new JSONObject(line));
            } catch (Exception e2) {
                o.put("raw", line);
            }
            return o.toString(0);
        }
    }

    public static String parseInternalNoErrorHandling(String rawLine) throws NoMessageMatch {
        String comment = null;
        if (rawLine.contains(" # ")) {
            String[] parts = rawLine.split(" # ");
            rawLine = parts[0];
            comment = parts[1];
        }
        JSONObject rawJson = new JSONObject(rawLine);
        Message message = messageFromString(rawLine);
        JSONObject parsed = null;
        if (message != null) {
            parsed = new JSONObject(message.jsonToString());

            // Auto-update pumpTimeSinceReset
            if (message instanceof TimeSinceResetResponse) {
                PumpStateSupplier.pumpTimeSinceReset = ((TimeSinceResetResponse) message)::getCurrentTime;
            }
        }

        JSONObject ret = new JSONObject();
        ret.put("parsed", parsed);
        if (comment != null) {
            rawJson.put("comment", comment);
        }
        if (Strings.isNullOrEmpty(rawJson.getString("btChar")) && message != null) {
            rawJson.put("guessedBtChar", CharacteristicGuesser.guessBestCharacteristic(rawLine, message.opCode()).getUuid().toString().replace("-", ""));
        }
        ret.put("raw", rawJson);

        return ret.toString(0);
    }

    public static Message messageFromString(String str) {
        JSONObject json = null;
        try {
            json = new JSONObject(str);
            String type = json.getString("type");
            String btChar = json.getString("btChar");
            String valueStr = json.getString("value");
            String[] extraValueStr = null;
            if (json.has("extraValueStr")) {
                JSONArray extraValueStrArr = json.getJSONArray("extraValueStr");
                extraValueStr = new String[extraValueStrArr.length()];
                for (int i = 0; i < extraValueStrArr.length(); i++) {
                    extraValueStr[i] = extraValueStrArr.getString(i);
                }
            }
            String ts = json.getString("ts");

            PumpResponseMessage resp = PumpResponseMessageBuilder.build(valueStr, btChar, extraValueStr);
            if (resp == null || resp.message().isEmpty()) {
                return null;
            }

            return resp.message().get();
        } catch (JSONException e) {
            L.d(TAG, e.getMessage());
            return null;
        }
    }
}
