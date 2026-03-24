package com.jwoglom.pumpx2.pump.messages.models;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

public class ApiVersion implements Comparable<ApiVersion> {

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

    @Override
    public int compareTo(ApiVersion other) {
        int cmp = Integer.compare(getMajor(), other.getMajor());
        if (cmp != 0) return cmp;
        return Integer.compare(getMinor(), other.getMinor());
    }

    public int compareTo(KnownApiVersion other) {
        return compareTo(other.get());
    }

    public boolean greaterThan(ApiVersion other) {
        return compareTo(other) > 0;
    }

    public boolean greaterThan(KnownApiVersion other) {
        return compareTo(other) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiVersion that = (ApiVersion) o;
        return major == that.major && minor == that.minor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor);
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
