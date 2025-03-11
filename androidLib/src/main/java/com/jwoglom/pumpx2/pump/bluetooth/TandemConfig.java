package com.jwoglom.pumpx2.pump.bluetooth;

import com.jwoglom.pumpx2.pump.messages.models.PairingCodeType;

import java.util.Optional;

/**
 * Configuration fields for {@link TandemPump} construction
 */
public class TandemConfig {
    private Optional<String> filterToBluetoothMac = Optional.empty();
    private Optional<PairingCodeType> pairingCodeType = Optional.empty();
    private Optional<Boolean> enablePeriodicTSR = Optional.empty();

    public TandemConfig() {}

    public TandemConfig withFilterToBluetoothMac(String mac) {
        this.filterToBluetoothMac = Optional.ofNullable(mac);
        return this;
    }

    public TandemConfig withPairingCodeType(PairingCodeType type) {
        this.pairingCodeType = Optional.ofNullable(type);
        return this;
    }

    public TandemConfig withEnablePeriodicTSR(Boolean enablePeriodicTSR) {
        this.enablePeriodicTSR = Optional.ofNullable(enablePeriodicTSR);
        return this;
    }

    public Optional<PairingCodeType> getPairingCodeType() {
        return pairingCodeType;
    }

    public Optional<String> getFilterToBluetoothMac() {
        return filterToBluetoothMac;
    }
    public Optional<Boolean> getEnablePeriodicTSR() {
        return enablePeriodicTSR;
    }
}
