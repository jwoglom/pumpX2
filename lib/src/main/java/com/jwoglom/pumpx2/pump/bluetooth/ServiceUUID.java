package com.jwoglom.pumpx2.pump.bluetooth;

import java.util.UUID;

public class ServiceUUID {
    // Pump-specific UUID used for all known functions
    public static final UUID PUMP_SERVICE_UUID = UUID.fromString("0000fdfb-0000-1000-8000-00805f9b34fb");

    // Generic Bluetooth Device Information service (DIS)
    public static final UUID DIS_SERVICE_UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb");
}
