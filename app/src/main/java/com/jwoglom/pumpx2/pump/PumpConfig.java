package com.jwoglom.pumpx2.pump;

/**
 * Temporary, hard-coded information about the pump to connect to.
 */
public class PumpConfig {
    public static String pumpSerial = PumpPrivateConfig.pumpSerial;
    public static String pumpMAC = PumpPrivateConfig.pumpMAC;

    public static String bluetoothName() {
        return "tslim X2 ***" + pumpSerial.substring(pumpSerial.length()-3);
    }
}
