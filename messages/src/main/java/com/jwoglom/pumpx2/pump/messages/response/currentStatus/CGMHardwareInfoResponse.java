package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ApiVersionRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CGMHardwareInfoRequest;

/**
 * Returns the G6 CGM transmitter ID.
 */
@MessageProps(
        opCode=97,
        size=17,
        type=MessageType.RESPONSE,
        request=CGMHardwareInfoRequest.class
)
public class CGMHardwareInfoResponse extends Message {
    /**
     * G6 transmitter ID
     */
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
        Validate.isTrue(raw.length == props().size());
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
