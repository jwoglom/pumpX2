package com.jwoglom.pumpx2.util.timber;

import com.jwoglom.pumpx2.shared.L;

import timber.log.Timber;

public class LConfigurator {
    private static boolean enabled = false;
    private static boolean disabled = false;

    /**
     * Since Timber is available to the androidLib package, configure the Java-only messages
     * package to associate its logging framework with Timber. This is called by default in
     * {@link com.jwoglom.pumpx2.pump.bluetooth.TandemBluetoothHandler} unless disableTimber
     * has been called.
     */
    public static void enableTimber() {
        if (disabled || enabled) {
            return;
        }

        L.getTimberDebug = (a, b, c) -> Timber.tag(a).d(b, c);
        L.getTimberInfo = (a, b, c) -> Timber.tag(a).i(b, c);
        L.getTimberWarning = (a, b, c) -> Timber.tag(a).w(b, c);
        L.getTimberWarningThrowable = (a, b, c, d) -> Timber.tag(a).w(b, c, d);
        L.getTimberError = (a, b, c) -> Timber.tag(a).e(b, c);
        L.getTimberErrorThrowable = (a, b, c, d) -> Timber.tag(a).e(b, c, d);

        L.getPrintln = (ignored) -> {};
        enabled = true;
        L.d("PumpX2", "Timber enabled for internal PumpX2 logging");
        Timber.i("PumpX2 Timber initialized");
    }

    /**
     * Disables Timber use and prints all log messages to System.out (the default)
     */
    public static void disableTimber() {
        if (disabled || enabled) {
            return;
        }
        L.getTimberDebug = (a, b, c) -> {};
        L.getTimberInfo = (a, b, c) -> {};
        L.getTimberWarning = (a, b, c) -> {};
        L.getTimberWarningThrowable = (a, b, c, d) -> {};
        L.getTimberError = (a, b, c) -> {};
        L.getTimberErrorThrowable = (a, b, c, d) -> {};

        L.getPrintln = System.out::println;
        disabled = true;
        L.d("PumpX2", "Timber disabled");
    }
}
