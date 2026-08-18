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
    /**
     * When enabled, periodically re-requests {@code ConnectionPriority.HIGH} while connected
     * (see {@link #periodicConnectionPriorityReassertIntervalMs}). Some pumps (e.g. the Mobi)
     * renegotiate down to a power-saving connection profile with a short supervision timeout
     * shortly after connecting, which can cause frequent connection drops; periodically
     * re-asserting HIGH priority widens the supervision window and reduces those drops.
     */
    private Optional<Boolean> enablePeriodicConnectionPriorityReassert = Optional.empty();
    /**
     * Interval (milliseconds) on which {@link #enablePeriodicConnectionPriorityReassert} re-requests
     * {@code ConnectionPriority.HIGH}. Defaults to 10 seconds if unset.
     */
    private Optional<Long> periodicConnectionPriorityReassertIntervalMs = Optional.empty();
    /**
     * Number of consecutive initial-connection hard-failure windows required before unbonding.
     * Null (default) means never unbond automatically.
     */
    private Optional<Integer> unbondAfterInitialConnectionHardFailuresCount = Optional.empty();
    /**
     * Number of initial auth/no-reply disconnects allowed within a rolling window
     * before automatic reconnect attempts are paused.
     */
    private Optional<Integer> initialDisconnectRetryMaxCount = Optional.empty();
    /**
     * Rolling time window (milliseconds) used for {@link #initialDisconnectRetryMaxCount}.
     */
    private Optional<Long> initialDisconnectRetryWindowMs = Optional.empty();

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

    public TandemConfig withEnablePeriodicConnectionPriorityReassert(Boolean enablePeriodicConnectionPriorityReassert) {
        this.enablePeriodicConnectionPriorityReassert = Optional.ofNullable(enablePeriodicConnectionPriorityReassert);
        return this;
    }

    public TandemConfig withPeriodicConnectionPriorityReassertIntervalMs(Long periodicConnectionPriorityReassertIntervalMs) {
        this.periodicConnectionPriorityReassertIntervalMs = Optional.ofNullable(periodicConnectionPriorityReassertIntervalMs);
        return this;
    }

    public TandemConfig withUnbondAfterInitialConnectionHardFailuresCount(Integer unbondAfterInitialConnectionHardFailuresCount) {
        this.unbondAfterInitialConnectionHardFailuresCount = Optional.ofNullable(unbondAfterInitialConnectionHardFailuresCount);
        return this;
    }

    public TandemConfig withInitialDisconnectRetryMaxCount(Integer initialDisconnectRetryMaxCount) {
        this.initialDisconnectRetryMaxCount = Optional.ofNullable(initialDisconnectRetryMaxCount);
        return this;
    }

    public TandemConfig withInitialDisconnectRetryWindowMs(Long initialDisconnectRetryWindowMs) {
        this.initialDisconnectRetryWindowMs = Optional.ofNullable(initialDisconnectRetryWindowMs);
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

    public Optional<Boolean> getEnablePeriodicConnectionPriorityReassert() {
        return enablePeriodicConnectionPriorityReassert;
    }

    public Optional<Long> getPeriodicConnectionPriorityReassertIntervalMs() {
        return periodicConnectionPriorityReassertIntervalMs;
    }

    public Optional<Integer> getUnbondAfterInitialConnectionHardFailuresCount() {
        return unbondAfterInitialConnectionHardFailuresCount;
    }

    public Optional<Integer> getInitialDisconnectRetryMaxCount() {
        return initialDisconnectRetryMaxCount;
    }

    public Optional<Long> getInitialDisconnectRetryWindowMs() {
        return initialDisconnectRetryWindowMs;
    }
}
