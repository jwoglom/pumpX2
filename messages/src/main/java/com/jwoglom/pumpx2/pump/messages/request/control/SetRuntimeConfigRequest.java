package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetRuntimeConfigResponse;

import org.apache.commons.lang3.Validate;

/**
 * Sets a pump runtime configuration value.
 *
 * Cargo layout (2 bytes):
 *   [0] config0 uint8
 *   [1] config1 uint8
 *
 * The t:connect Android app currently only ever sends {0, 0}; the two bytes are
 * exposed here so other values can be tried. Derived from the decompiled app
 * (SetRuntimeConfigRequest, opcode 122).
 */
@MessageProps(
    opCode=122,
    size=2,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=SetRuntimeConfigResponse.class,
    signed=true
)
public class SetRuntimeConfigRequest extends Message {
    private int config0;
    private int config1;

    public SetRuntimeConfigRequest() {
        this(0, 0);
    }

    public SetRuntimeConfigRequest(int config0, int config1) {
        this.config0 = config0;
        this.config1 = config1;
        this.cargo = buildCargo(config0, config1);
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.config0 = raw[0] & 0xFF;
        this.config1 = raw[1] & 0xFF;
    }

    public static byte[] buildCargo(int config0, int config1) {
        return new byte[]{ (byte) config0, (byte) config1 };
    }

    public int getConfig0() {
        return config0;
    }

    public int getConfig1() {
        return config1;
    }
}
