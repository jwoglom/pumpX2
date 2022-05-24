package com.jwoglom.pumpx2.pump.messages;

import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.AlarmStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.AlertStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.request.CentralChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.request.PumpChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.response.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.AlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.ApiVersionResponse;
import com.jwoglom.pumpx2.pump.messages.response.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.PumpChallengeResponse;
import com.jwoglom.pumpx2.shared.L;

import org.apache.commons.codec.binary.Hex;

import java.util.HashMap;
import java.util.Map;

public enum Messages {
    API_VERSION(ApiVersionRequest.class, ApiVersionResponse.class),
    CENTRAL_CHALLENGE(CentralChallengeRequest.class, CentralChallengeResponse.class),
    PUMP_CHALLENGE(PumpChallengeRequest.class, PumpChallengeResponse.class),
    ALARM_STATUS(AlarmStatusRequest.class, AlarmStatusResponse.class),
    ALERT_STATUS(AlertStatusRequest.class, AlertStatusResponse.class),

    ;

    private static final String TAG = "X2-Messages";

    public static Map<Integer, Class<? extends Message>> OPCODES = new HashMap<>();
    public static Map<Integer, Messages> REQUESTS = new HashMap<>();
    public static Map<Integer, Messages> RESPONSES = new HashMap<>();

    static {
        for (Messages m : Messages.values()) {
            OPCODES.put(m.requestOpCode, m.requestClass);
            REQUESTS.put(m.requestOpCode, m);

            OPCODES.put(m.responseOpCode, m.responseClass);
            RESPONSES.put(m.responseOpCode, m);
        }
    }

    public static Message parse(byte[] data, int opCode) {
        try {
            Message msg = OPCODES.get(opCode).newInstance();
            msg.parse(data);
            return msg;
        } catch (Exception e) {
            L.w(TAG, "Unable to invoke parse of data: " + Hex.encodeHexString(data) + " opCode: " + opCode);
            e.printStackTrace();
            return null;
        }
    }

    private final int requestOpCode;
    private final Class<? extends Message> requestClass;
    private final int responseOpCode;
    private final Class<? extends Message> responseClass;
    Messages(Class<? extends Message> requestClass, Class<? extends Message> responseClass) {
        this.requestOpCode = requestClass.getAnnotation(MessageProps.class).opCode();
        this.requestClass = requestClass;
        this.responseOpCode = responseClass.getAnnotation(MessageProps.class).opCode();
        this.responseClass = responseClass;
    }

    public int requestOpCode() {
        return requestOpCode;
    }
    public Class<? extends Message> requestClass() {
        return requestClass;
    }

    public int responseOpCode() {
        return responseOpCode;
    }
    public Class<? extends Message> responseClass() {
        return responseClass;
    }

    public Message request() {
        try {
            return requestClass.newInstance();
        } catch (IllegalAccessException | InstantiationException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Message response() {
        try {
            return responseClass.newInstance();
        } catch (IllegalAccessException | InstantiationException | ClassCastException e) {
            e.printStackTrace();
            return null;
        }
    }
}
