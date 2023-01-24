package com.jwoglom.pumpx2.pump.messages.bluetooth;

import java.util.UUID;

public class ServiceUUID {
    // Pump-specific UUID used for all known functions
    public static final UUID PUMP_SERVICE_UUID = UUID.fromString("0000fdfb-0000-1000-8000-00805f9b34fb");

    // Generic Bluetooth Device Information service (DIS)
    public static final UUID DIS_SERVICE_UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");

    public static String which(UUID uuid) {
        if (uuid.equals(PUMP_SERVICE_UUID)) {
            return "PUMP_SERVICE";
        } else if (uuid.equals(DIS_SERVICE_UUID)) {
            return "DIS_SERVICE";
        } else {
            return "unknown (" + uuid + ")";
        }
    }
}
