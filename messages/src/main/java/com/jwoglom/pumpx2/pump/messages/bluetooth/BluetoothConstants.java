package com.jwoglom.pumpx2.pump.messages.bluetooth;

public final class BluetoothConstants {
    public final static String DEVICE_NAME_TSLIM_X2 = "tslim X2";

    public static boolean isTandemBluetoothDevice(String deviceName) {
        return deviceName.startsWith(DEVICE_NAME_TSLIM_X2);
    }
}
