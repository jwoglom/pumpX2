package com.jwoglom.pumpx2.pump.messages.models;

import org.apache.commons.lang3.Validate;

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

    public String toString() {
        return "ApiVersion(major=" + major + ", minor=" + minor + ")";
    }

    public String serialize() {
        return major + "," + minor;
    }

    public static ApiVersion deserialize(String s) {
        if (s == null || s.isEmpty()) return null;
        String[] parts = s.split(",");
        Validate.isTrue(parts.length == 2);
        return new ApiVersion(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }
}
