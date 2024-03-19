package com.jwoglom.pumpx2.pump.messages.models;

public enum KnownApiVersion {
    // v2.1 is the API version used by software v7.1 and v7.4. It is the earliest known API version,
    // previous pump firmware did not have Bluetooth connection compatibility.
    API_V2_1(2, 1),
    // v2.5 is the API version used by software v7.6 and includes remote bolus
    API_V2_5(2, 5),
    // v3.2 is the API version used by software v7.7 and utilizes a 6-character numeric pairing PIN
    API_V3_2(3, 2),
    // v3.4 is the API version used by software v7.8
    API_V3_4(3, 4),
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
