package com.jwoglom.pumpx2.pump.bluetooth;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.authentication.CentralChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.request.authentication.PumpChallengeRequest;
import com.jwoglom.pumpx2.pump.messages.response.authentication.CentralChallengeResponse;
import com.jwoglom.pumpx2.pump.messages.response.authentication.PumpChallengeResponse;

import java.util.UUID;

public class CharacteristicUUID {
    // For reading pump state
    public static final UUID CURRENT_STATUS_CHARACTERISTICS = UUID.fromString("7B83FFF6-9F77-4E5C-8064-AAE2C24838B9");
    public static final UUID QUALIFYING_EVENTS_CHARACTERISTICS = UUID.fromString("7B83FFF7-9F77-4E5C-8064-AAE2C24838B9");
    public static final UUID HISTORY_LOG_CHARACTERISTICS = UUID.fromString("7B83FFF8-9F77-4E5C-8064-AAE2C24838B9");

    // For authentication
    public static final UUID AUTHORIZATION_CHARACTERISTICS = UUID.fromString("7B83FFF9-9F77-4E5C-8064-AAE2C24838B9");


    // For signed messages
    public static final UUID CONTROL_CHARACTERISTICS = UUID.fromString("7B83FFFC-9F77-4E5C-8064-AAE2C24838B9");
    public static final UUID CONTROL_STREAM_CHARACTERISTICS = UUID.fromString("7B83FFFD-9F77-4E5C-8064-AAE2C24838B9");
    public static final UUID SERVICE_CHANGED_CHARACTERISTICS = UUID.fromString("00002A05-0000-1000-8000-00805F9B34FB"); // FIXED: extra 0
    public static final UUID NOTIFICATION_CCCD = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB"); // FIXED: extra 0

    // Generic Bluetooth characteristics
    public static final UUID MANUFACTURER_NAME_CHARACTERISTIC_UUID = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb");
    public static final UUID MODEL_NUMBER_CHARACTERISTIC_UUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb");


    public static UUID determine(Message message) {
        if (message instanceof CentralChallengeRequest ||
            message instanceof CentralChallengeResponse ||
            message instanceof PumpChallengeRequest ||
            message instanceof PumpChallengeResponse) {
            return AUTHORIZATION_CHARACTERISTICS;
        }

        if (message.signed()) {
            return CONTROL_CHARACTERISTICS;
        }

        return CURRENT_STATUS_CHARACTERISTICS;
    }
}
