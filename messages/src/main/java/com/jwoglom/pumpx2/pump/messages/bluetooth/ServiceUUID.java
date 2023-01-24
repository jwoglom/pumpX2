package com.jwoglom.pumpx2.pump.messages.bluetooth;

import java.util.UUID;

public class ServiceUUID {
    // Pump-specific UUID used for all known functions
    public static final UUID PUMP_SERVICE_UUID = UUID.fromString("0000fdfb-0000-1000-8000-00805f9b34fb");

    // Bluetooth Device Information service (DIS)
    public static final UUID DIS_SERVICE_UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");

    // Generic Access service
    public static final UUID GENERIC_ACCESS_SERVICE_UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");

    // Generic Attribute service
    public static final UUID GENERIC_ATTRIBUTE_SERVICE_UUID = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");

    public static String which(UUID uuid) {
        if (uuid.equals(PUMP_SERVICE_UUID)) {
            return "PUMP_SERVICE";
        } else if (uuid.equals(DIS_SERVICE_UUID)) {
            return "DIS_SERVICE";
        } else if (uuid.equals(GENERIC_ACCESS_SERVICE_UUID)) {
            return "GENERIC_ACCESS_SERVICE";
        } else if (uuid.equals(GENERIC_ATTRIBUTE_SERVICE_UUID)) {
            return "GENERIC_ATTRIBUTE_SERVICE";
        } else {
            return "unknown (" + uuid + ")";
        }
    }
}
