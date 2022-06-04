package com.jwoglom.pumpx2.pump.messages;

import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlarmStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.AlertStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMHardwareInfoRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMStatusRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.CentralChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ControlIQIOBRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.NonControlIQIOBRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpFeaturesRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpGlobalsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.PumpSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMHardwareInfoResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CGMStatusResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ControlIQIOBResponse;
import com.jwoglom.pumpx2.pump.messages.response.ErrorResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.NonControlIQIOBResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpFeaturesResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpGlobalsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.PumpSettingsResponse;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBatteryV1Request;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBatteryV1Response;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CurrentBatteryV2Request;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.CurrentBatteryV2Response;
// IMPORT_END
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
    CGM_HARDWARE_INFO(CGMHardwareInfoRequest.class, CGMHardwareInfoResponse.class),
    CONTROL_IQ_IOB(ControlIQIOBRequest.class, ControlIQIOBResponse.class),
    NON_CONTROL_IQ_IOB(NonControlIQIOBRequest.class, NonControlIQIOBResponse.class),
    PUMP_FEATURES(PumpFeaturesRequest.class, PumpFeaturesResponse.class),
    PUMP_GLOBALS(PumpGlobalsRequest.class, PumpGlobalsResponse.class),
    PUMP_SETTINGS(PumpSettingsRequest.class, PumpSettingsResponse.class),
    CGM_STATUS(CGMStatusRequest.class, CGMStatusResponse.class),
    CURRENT_BATTERY_V1(CurrentBatteryV1Request.class, CurrentBatteryV1Response.class),
    CURRENT_BATTERY_V2(CurrentBatteryV2Request.class, CurrentBatteryV2Response.class),
    // MESSAGES_END
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

        OPCODES.put(new ErrorResponse().opCode(), ErrorResponse.class);
    }

    public static Message parse(byte[] data, int opCode) {
        try {
            Class<? extends Message> clazz = OPCODES.get(opCode);
            if (clazz == null) {
                L.w(TAG, "Unable to find message for opCode: " + opCode +" with data: " + Hex.encodeHexString(data));
                return null;
            }
            Message msg = clazz.newInstance();
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
