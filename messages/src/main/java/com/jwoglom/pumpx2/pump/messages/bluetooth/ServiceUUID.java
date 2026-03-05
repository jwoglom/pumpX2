package com.jwoglom.pumpx2.pump.messages.bluetooth;

import java.util.UUID;

public class ServiceUUID {
    // Tandem TIP service UUID (aka Pump service in this project)
    public static final UUID TIP_SERVICE_UUID = UUID.fromString("0000fdfb-0000-1000-8000-00805f9b34fb");
    public static final UUID PUMP_SERVICE_UUID = TIP_SERVICE_UUID;

    // Tandem TDU service UUID (used by Mobi app alongside TIP)
    public static final UUID TDU_SERVICE_UUID = UUID.fromString("0000fdfa-0000-1000-8000-00805f9b34fb");

    // Bluetooth Device Information service (DIS)
    public static final UUID DIS_SERVICE_UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");

    // Generic Access service
    public static final UUID GENERIC_ACCESS_SERVICE_UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb");

    // Generic Attribute service
    public static final UUID GENERIC_ATTRIBUTE_SERVICE_UUID = UUID.fromString("00001801-0000-1000-8000-00805f9b34fb");

    public static String which(UUID uuid) {
        if (uuid.equals(PUMP_SERVICE_UUID)) {
            return "PUMP_SERVICE";
        } else if (uuid.equals(TDU_SERVICE_UUID)) {
            return "TDU_SERVICE";
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
