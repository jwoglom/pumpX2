package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;

import kotlin.collections.ArraysKt;

@MessageProps(
    opCode=33,
    size=4,
    type=MessageType.RESPONSE,
    request=ApiVersionRequest.class
)
public class ApiVersionResponse extends Message {
    private int majorVersion;
    private int minorVersion;

    public ApiVersionResponse() {}

    public ApiVersionResponse(int majorVersion, int minorVersion) {
        this.cargo = buildCargo(majorVersion, minorVersion);
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    private static byte[] buildCargo(int majorVersion, int minorVersion) {
        byte[] cargo = new byte[4];
        System.arraycopy(ArraysKt.plus(
                Bytes.firstTwoBytesLittleEndian(majorVersion),
                Bytes.firstTwoBytesLittleEndian(minorVersion)), 0, cargo, 0, 4);

        return cargo;
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        majorVersion = Bytes.readShort(raw, 0);
        minorVersion = Bytes.readShort(raw, 2);
        cargo = raw;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public static class ApiVersion {
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

        public boolean greaterThanOrEqual(ApiVersion other) {
            return getMajor() > other.getMajor() || (getMajor() == other.getMajor() && getMinor() >= other.getMinor());
        }
    }

    public ApiVersion getApiVersion() {
        return new ApiVersion(getMajorVersion(), getMinorVersion());
    }
}
