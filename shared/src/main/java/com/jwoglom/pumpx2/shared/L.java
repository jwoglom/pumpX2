package com.jwoglom.pumpx2.shared;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class L {
    public static final String LOG_PREFIX = "PumpX2";

    // Giant hack to allow this logging class to be used across Android and non-Android code
    public static BiConsumer<String, String> getTimberDebug = (a, b) -> {};
    public static BiConsumer<String, String> getTimberInfo = (a, b) -> {};
    public static BiConsumer<String, String> getTimberWarning = (a, b) -> {};
    public static TriConsumer<Throwable, String, String> getTimberWarningThrowable = (a, b, c) -> {};
    public static BiConsumer<String, String> getTimberError = (a, b) -> {};
    public static TriConsumer<Throwable, String, String> getTimberErrorThrowable = (a, b, c) -> {};

    public static Consumer<String> getPrintln = System.out::println;

    // Debug
    public static void d(String tag, String out) {
        getPrintln.accept(LOG_PREFIX + ":DEBUG: " + tag + ": " + out);
        try {
            getTimberDebug.accept(tag + ": %s", out);
        } catch (RuntimeException e) {

        }
    }

    // Info
    public static void i(String tag, String out) {
        getPrintln.accept(LOG_PREFIX + ":INFO: " + tag + ": " + out);
        try {
            getTimberInfo.accept(tag + ": %s", out);
        } catch (RuntimeException e) {

        }
    }
    // Warning
    public static void w(String tag, String out) {
        getPrintln.accept(LOG_PREFIX + ":WARN: " + tag + ": " + out);
        try {
            getTimberWarning.accept(tag + ": %s", out);
        } catch (RuntimeException e) {

        }
    }

    public static void w(String tag, Throwable thrw) {
        getPrintln.accept(LOG_PREFIX + ":WARN: " + tag + ": " + thrw);
        try {
            getTimberWarningThrowable.accept(thrw, tag + ": %s", thrw.toString());
        } catch (RuntimeException e) {
        }
    }

    public static void w(String tag, String out, Throwable thrw) {
        getPrintln.accept(LOG_PREFIX + ":WARN: " + tag + ": " + out + ": " + thrw);
        try {
            getTimberWarningThrowable.accept(thrw, tag + ": %s", out);
        } catch (RuntimeException e) {
        }
    }

    public static void e(String tag, String out) {
        getPrintln.accept(LOG_PREFIX + ":ERROR: " + tag + ": " + out);
        try {
            getTimberError.accept(tag + ": %s", out);
        } catch (RuntimeException e) {
        }
    }

    public static void e(String tag, Throwable thrw) {
        getPrintln.accept(LOG_PREFIX + ":ERROR: " + tag + ": " + thrw);
        try {
            getTimberErrorThrowable.accept(thrw, tag + ": %s", thrw.toString());
        } catch (RuntimeException e) {
        }
    }

    public static void e(String tag, String out, Throwable thrw) {
        getPrintln.accept(LOG_PREFIX + ":ERROR: " + tag + ": " + out + ": " + thrw);
        try {
            getTimberErrorThrowable.accept(thrw, tag + ": %s", out);
        } catch (RuntimeException e) {
        }
    }
}
