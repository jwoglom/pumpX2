package com.jwoglom.pumpx2.pump.messages.models;

public enum KnownApiVersion {
    // v2.1 is the API version used by software v7.1 and v7.4
    API_V2_1(2, 1),
    // v2.5 is the API version used by software v7.6 and includes remote bolus
    API_V2_5(2, 5),
    // Represents known messages from the app that we can't parse with known firmware
    API_FUTURE(99, 99)
    ;

    private int major;
    private int minor;
    KnownApiVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public ApiVersion get() {
        return new ApiVersion(major, minor);
    }
}
