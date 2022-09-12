package com.jwoglom.pumpx2.pump.messages.bluetooth;

import com.jwoglom.pumpx2.pump.messages.models.ApiVersion;

import java.util.function.Supplier;

public class PumpStateSupplier {

    public static Supplier<String> authenticationKey = null;
    public static Supplier<Long> pumpTimeSinceReset = null;
    public static Supplier<ApiVersion> pumpApiVersion = null;
    public static Supplier<Boolean> controlIQSupported = () -> false;
    public static Supplier<Boolean> actionsAffectingInsulinDeliveryEnabled = () -> false;
}
