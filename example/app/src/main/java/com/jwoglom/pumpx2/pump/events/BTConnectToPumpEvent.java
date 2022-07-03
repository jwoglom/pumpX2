package com.jwoglom.pumpx2.pump.events;

public class BTConnectToPumpEvent extends Event {
    private String deviceName;
    private String macAddress;

    public BTConnectToPumpEvent(String deviceName, String macAddress) {
        this.deviceName = deviceName;
        this.macAddress = macAddress;
    }

    public String deviceName() { return deviceName; }
    public String macAddress() { return macAddress; }

    public String toString() {
        return "BTConnectToPumpEvent(deviceName="+deviceName+", macAddress="+macAddress+")";
    }
}
