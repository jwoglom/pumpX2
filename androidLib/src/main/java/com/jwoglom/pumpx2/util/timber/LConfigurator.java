package com.jwoglom.pumpx2.util.timber;

import com.jwoglom.pumpx2.shared.L;

import timber.log.Timber;

public class LConfigurator {
    private static boolean disabled = false;

    /**
     * Since Timber is available to the androidLib package, configure the Java-only messages
     * package to associate its logging framework with Timber. This is called by default in
     * {@link com.jwoglom.pumpx2.pump.bluetooth.TandemBluetoothHandler} unless disableTimber
     * has been called.
     */
    public static void enableTimber() {
        if (disabled) {
            return;
        }

        L.getTimberDebug = Timber::d;
        L.getTimberInfo = Timber::i;
        L.getTimberWarning = Timber::w;
        L.getTimberWarningThrowable = Timber::w;
        L.getTimberError = Timber::e;
        L.getTimberErrorThrowable = Timber::e;

        L.getPrintln = (ignored) -> {};
    }

    /**
     * Disables Timber use and prints all log messages to System.out (the default)
     */
    public static void disableTimber() {
        L.getTimberDebug = (a, b) -> {};
        L.getTimberInfo = (a, b) -> {};
        L.getTimberWarning = (a, b) -> {};
        L.getTimberWarningThrowable = (a, b, c) -> {};
        L.getTimberError = (a, b) -> {};
        L.getTimberErrorThrowable = (a, b, c) -> {};

        L.getPrintln = System.out::println;
        disabled = true;
    }
}
