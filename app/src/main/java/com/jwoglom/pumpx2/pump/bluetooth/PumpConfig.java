package com.jwoglom.pumpx2.pump.bluetooth;

public class PumpConfig {

    public static String pumpSerial = "***REMOVED***";
    public static String pumpMAC = "***REMOVED***";

    public static String bluetoothName() {
        return "tslim X2 ***" + pumpSerial.substring(pumpSerial.length()-3);
    }
}
