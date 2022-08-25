package com.jwoglom.pumpx2.shared;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class L {

    // Giant hack to allow this logging class to be used across Android and non-Android code
    public static BiConsumer<String, String> getAndroidDebug = null;
    public static BiConsumer<String, String> getAndroidWarning = null;
    public static TriConsumer<String, String, Throwable> getAndroidWarningThrowable = null;
    public static BiConsumer<String, String> getAndroidLogError = null;
    public static TriConsumer<String, String, Throwable> getAndroidLogErrorThrowable = null;

    public static Consumer<String> getPrintln = System.out::println;

    // Debug
    public static void d(String tag, String out) {
        getPrintln.accept("DEBUG: " + tag + ": " + out);
        try {
            getAndroidDebug.accept(tag, out);
        } catch (RuntimeException e) {

        }
    }
    // Warning
    public static void w(String tag, String out) {
        getPrintln.accept("WARN: " + tag + ": " + out);
        try {
            getAndroidWarning.accept(tag, out);
        } catch (RuntimeException e) {

        }
    }

    public static void w(String tag, Throwable out) {
        getPrintln.accept("WARN: " + tag + ": " + out);
        try {
            getAndroidWarningThrowable.accept(tag, out.toString(), out);
        } catch (RuntimeException e) {
        }
    }

    public static void w(String tag, String out, Throwable thrw) {
        getPrintln.accept("WARN: " + tag + ": " + out + ": " + thrw);
        try {
            getAndroidWarningThrowable.accept(tag, out, thrw);
        } catch (RuntimeException e) {
        }
    }

    public static void e(String tag, String out) {
        getPrintln.accept("ERROR: " + tag + ": " + out);
        try {
            getAndroidLogError.accept(tag, out);
        } catch (RuntimeException e) {
        }
    }

    public static void e(String tag, Throwable out) {
        getPrintln.accept("ERROR: " + tag + ": " + out);
        try {
            getAndroidLogErrorThrowable.accept(tag, out.toString(), out);
        } catch (RuntimeException e) {
        }
    }

    public static void e(String tag, String out, Throwable thrw) {
        getPrintln.accept("ERROR: " + tag + ": " + out + ": " + thrw);
        try {
            getAndroidLogErrorThrowable.accept(tag, out, thrw);
        } catch (RuntimeException e) {
        }
    }

    private static boolean isJUnitTest() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return true;
            }
        }
        return false;
    }
}
