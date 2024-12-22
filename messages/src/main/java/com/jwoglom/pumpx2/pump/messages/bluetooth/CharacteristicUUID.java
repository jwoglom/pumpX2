package com.jwoglom.pumpx2.pump.messages.bluetooth;

import com.google.common.collect.ImmutableList;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.response.historyLog.HistoryLogStreamResponse;

import java.util.List;
import java.util.UUID;

/**
 * TODO: refactor uses to {@link Characteristic}
 */
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

    // Service Changed generic BT characteristic (on GENERIC_ATTRIBUTE_SERVICE_UUID)
    public static final UUID SERVICE_CHANGED_CHARACTERISTICS = UUID.fromString("00002A05-0000-1000-8000-00805F9B34FB"); // There is an extra 0 in this UUID from Tandem (000002A05-0000-1000-8000-00805F9B34FB)

    // Client Characteristic Configuration; unused
    public static final UUID NOTIFICATION_CCCD = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB"); // There is an extra 0 in this UUID from Tandem (000002902-0000-1000-8000-00805F9B34FB)

    // Generic Bluetooth characteristics (DIS_SERVICE_UUID)
    public static final UUID MANUFACTURER_NAME_CHARACTERISTIC_UUID = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb");
    public static final UUID MODEL_NUMBER_CHARACTERISTIC_UUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb");
    public static final UUID SERIAL_NUMBER_CHARACTERISTIC_UUID = UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb");
    public static final UUID SOFTWARE_REV_CHARACTERISTIC_UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");

    // Generic Access Bluetooth characteristics (GENERIC_ACCESS_SERVICE)
    public static final UUID GENERIC_ACCESS_DEVICE_NAME = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
    public static final UUID GENERIC_ACCESS_APPEARANCE = UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb");
    public static final UUID GENERIC_ACCESS_PREFERRED_CONN_PARAMETERS = UUID.fromString("00002a04-0000-1000-8000-00805f9b34fb");
    public static final UUID GENERIC_ACCESS_CENTRAL_ADDRESS_RESOLUTION = UUID.fromString("00002aa6-0000-1000-8000-00805f9b34fb");



    public static final List<UUID> ENABLED_NOTIFICATIONS = ImmutableList.of(
            CURRENT_STATUS_CHARACTERISTICS,
            QUALIFYING_EVENTS_CHARACTERISTICS,
            HISTORY_LOG_CHARACTERISTICS,

            AUTHORIZATION_CHARACTERISTICS,

            CONTROL_CHARACTERISTICS,
            CONTROL_STREAM_CHARACTERISTICS,

            // NOTE: on GENERIC_ATTRIBUTE_SERVICE_UUID
            SERVICE_CHANGED_CHARACTERISTICS
    );

    public static UUID determine(Message message) {
        if (message.getCharacteristic() == Characteristic.AUTHORIZATION) {
            return AUTHORIZATION_CHARACTERISTICS;
        }

        if (message instanceof HistoryLogStreamResponse) {
            return HISTORY_LOG_CHARACTERISTICS;
        }

        if (message.signed() || message.getCharacteristic() == Characteristic.CONTROL) {
            return CONTROL_CHARACTERISTICS;
        }

        return CURRENT_STATUS_CHARACTERISTICS;
    }

    public static String which(UUID uuid) {
        if (CURRENT_STATUS_CHARACTERISTICS.equals(uuid)) {
            return "CURRENT_STATUS";
        } else if (QUALIFYING_EVENTS_CHARACTERISTICS.equals(uuid)) {
            return "QUALIFYING_EVENTS";
        } else if (HISTORY_LOG_CHARACTERISTICS.equals(uuid)) {
            return "HISTORY_LOG";
        } else if (AUTHORIZATION_CHARACTERISTICS.equals(uuid)) {
            return "AUTHORIZATION";
        } else if (CONTROL_CHARACTERISTICS.equals(uuid)) {
            return "CONTROL";
        } else if (CONTROL_STREAM_CHARACTERISTICS.equals(uuid)) {
            return "CONTROL_STREAM";
        } else if (SERVICE_CHANGED_CHARACTERISTICS.equals(uuid)) {
            return "SERVICE_CHANGED";
        } else if (NOTIFICATION_CCCD.equals(uuid)) {
            return "NOTIFICATION";

        // DIS_SERVICE_UUID
        } else if (MANUFACTURER_NAME_CHARACTERISTIC_UUID.equals(uuid)) {
            return "MANUFACTURER_NAME";
        } else if (MODEL_NUMBER_CHARACTERISTIC_UUID.equals(uuid)) {
            return "MODEL_NUMBER";
        } else if (SERIAL_NUMBER_CHARACTERISTIC_UUID.equals(uuid)) {
            return "SERIAL_NUMBER";
        } else if (SOFTWARE_REV_CHARACTERISTIC_UUID.equals(uuid)) {
            return "SOFTWARE_REV";

        // GENERIC_ACCESS_SERVICE
        } else if (GENERIC_ACCESS_DEVICE_NAME.equals(uuid)) {
            return "DEVICE_NAME";
        } else if (GENERIC_ACCESS_APPEARANCE.equals(uuid)) {
            return "APPEARANCE";
        } else if (GENERIC_ACCESS_PREFERRED_CONN_PARAMETERS.equals(uuid)) {
            return "PREFERRED_CONN_PARAMETERS";
        } else if (GENERIC_ACCESS_CENTRAL_ADDRESS_RESOLUTION.equals(uuid)) {
            return "CENTRAL_ADDRESS_RESOLUTION";
        }

        return "unknown (" + uuid + ")";
    }
}
