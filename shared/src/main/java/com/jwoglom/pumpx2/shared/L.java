package com.jwoglom.pumpx2.shared;

import java.util.function.BiConsumer;

public class L {

    // Giant hack to allow this logging class to be used across Android and non-Android code
    public static BiConsumer<String, String> getAndroidLogW = null;
    public static TriConsumer<String, String, Throwable> getAndroidLogWThrowable = null;
    public static BiConsumer<String, String> getAndroidLogE = null;
    public static TriConsumer<String, String, Throwable> getAndroidLogEThrowable = null;

    public static void w(String tag, String out) {
        System.out.println("WARN: " + tag + ": " + out);
        try {
            getAndroidLogW.accept(tag, out);
        } catch (RuntimeException e) {

        }
    }

    public static void w(String tag, Throwable out) {
        System.out.println("WARN: " + tag + ": " + out);
        try {
            getAndroidLogWThrowable.accept(tag, out.toString(), out);
        } catch (RuntimeException e) {
        }
    }

    public static void w(String tag, String out, Throwable thrw) {
        System.out.println("WARN: " + tag + ": " + out + ": " + thrw);
        try {
            getAndroidLogWThrowable.accept(tag, out, thrw);
        } catch (RuntimeException e) {
        }
    }

    public static void e(String tag, String out) {
        System.out.println("ERROR: " + tag + ": " + out);
        try {
            getAndroidLogE.accept(tag, out);
        } catch (RuntimeException e) {
        }
    }

    public static void e(String tag, Throwable out) {
        System.out.println("ERROR: " + tag + ": " + out);
        try {
            getAndroidLogEThrowable.accept(tag, out.toString(), out);
        } catch (RuntimeException e) {
        }
    }

    public static void e(String tag, String out, Throwable thrw) {
        System.out.println("ERROR: " + tag + ": " + out + ": " + thrw);
        try {
            getAndroidLogEThrowable.accept(tag, out, thrw);
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
