package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.models.ApiVersion;
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
        System.arraycopy(Bytes.combine(
                Bytes.firstTwoBytesLittleEndian(majorVersion),
                Bytes.firstTwoBytesLittleEndian(minorVersion)), 0, cargo, 0, 4);

        return cargo;
    }

    public void parse(byte[] raw) {
        Validate.isTrue(raw.length == props().size());
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



    public ApiVersion getApiVersion() {
        return new ApiVersion(getMajorVersion(), getMinorVersion());
    }
}
