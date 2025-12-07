package com.jwoglom.pumpx2.pump.messages.bluetooth;

public final class BluetoothConstants {
    public final static String DEVICE_NAME_TSLIM_X2 = "tslim X2";

    // note, the Bluetooth DIS device name still identifies as a X2.
    // Only the user-visible name is labeled as "Tandem Mobi"
    public final static String DEVICE_NAME_TANDEM_MOBI = "Tandem Mobi";

    public static boolean isTandemBluetoothDevice(String deviceName) {
        return deviceName.startsWith(DEVICE_NAME_TSLIM_X2) || deviceName.startsWith(DEVICE_NAME_TANDEM_MOBI);
    }
}
