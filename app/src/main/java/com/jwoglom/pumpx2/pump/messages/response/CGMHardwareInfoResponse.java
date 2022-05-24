package com.jwoglom.pumpx2.pump.messages.response;

import com.google.common.base.Preconditions;
import com.jwoglom.pumpx2.pump.messages.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.request.ApiVersionRequest;

import java.nio.charset.StandardCharsets;

import kotlin.collections.ArraysKt;

@MessageProps(
        opCode=97,
        size=17,
        type=MessageType.RESPONSE,
        request=ApiVersionRequest.class
)
public class CGMHardwareInfoResponse extends Message {
    private String hardwareInfoString;
    private int lastByte;

    public CGMHardwareInfoResponse() {}

    public CGMHardwareInfoResponse(String hardwareInfoString, int lastByte) {
        this.cargo = buildCargo(hardwareInfoString, lastByte);
        this.hardwareInfoString = hardwareInfoString;
        this.lastByte = lastByte;
    }

    private static byte[] buildCargo(String hardwareInfoString, int lastByte) {
        return Bytes.combine(
                Bytes.writeString(hardwareInfoString, 16),
                new byte[]{(byte) lastByte});
    }

    public void parse(byte[] raw) {
        Preconditions.checkArgument(raw.length == props().size());
        hardwareInfoString = Bytes.readString(raw, 0, 16);
        lastByte = raw[16];
        cargo = raw;
    }

    public String getHardwareInfoString() {
        return hardwareInfoString;
    }

    public int getLastByte() {
        return lastByte;
    }
}
