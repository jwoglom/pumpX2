package com.jwoglom.pumpx2.shared;

import android.util.Log;

public class L {
    public static void w(String tag, String out) {
        try {
            Log.w(tag, out);
        } catch (RuntimeException e) {
            System.out.println("WARN: " + tag + ": " + out);
        }
    }

    public static void w(String tag, Throwable out) {
        try {
            Log.w(tag, out);
        } catch (RuntimeException e) {
            System.out.println("WARN: " + tag + ": " + out);
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
