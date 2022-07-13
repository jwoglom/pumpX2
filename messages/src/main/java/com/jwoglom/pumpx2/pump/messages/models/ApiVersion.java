package com.jwoglom.pumpx2.pump.messages.models;

public class ApiVersion {

    private final int major;
    private final int minor;
    public ApiVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public boolean greaterThan(ApiVersion other) {
        return getMajor() > other.getMajor() || (getMajor() == other.getMajor() && getMinor() > other.getMinor());
    }

    public boolean greaterThan(KnownApiVersion other) {
        return greaterThan(other.get());
    }
}
