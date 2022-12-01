package com.jwoglom.pumpx2.pump.messages.models;

public class InsulinUnit {
    // 1000 = 1 unit
    public static double from1000To1(Long units) {
        return (units.doubleValue() / 1000);
    }

    public static long from1To1000(Float units) {
        return Float.valueOf(units * 1000).longValue();
    }

    public static long from1To1000(Double units) {
        return Double.valueOf(units * 1000).longValue();
    }
}
