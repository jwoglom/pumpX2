package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetConfigurableFeatureResponse;

import org.apache.commons.lang3.Validate;

/**
 * Enables or disables a pump configurable feature, addressed by group and feature index.
 *
 * Cargo layout (3 bytes):
 *   [0] groupIndex   uint8
 *   [1] featureIndex uint8
 *   [2] featureState uint8  - 0 = disabled, 1 = enabled
 *
 * Derived from the decompiled t:connect Android app (SetConfigurableFeatureRequest, opcode 116).
 */
@MessageProps(
    opCode=116,
    size=3,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=SetConfigurableFeatureResponse.class,
    signed=true
)
public class SetConfigurableFeatureRequest extends Message {
    private int groupIndex;
    private int featureIndex;
    private boolean featureState;

    public SetConfigurableFeatureRequest() {}

    public SetConfigurableFeatureRequest(int groupIndex, int featureIndex, boolean featureState) {
        this.groupIndex = groupIndex;
        this.featureIndex = featureIndex;
        this.featureState = featureState;
        this.cargo = buildCargo(groupIndex, featureIndex, featureState);
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.groupIndex = raw[0] & 0xFF;
        this.featureIndex = raw[1] & 0xFF;
        this.featureState = (raw[2] & 0xFF) != 0;
    }

    public static byte[] buildCargo(int groupIndex, int featureIndex, boolean featureState) {
        return new byte[]{ (byte) groupIndex, (byte) featureIndex, (byte) (featureState ? 1 : 0) };
    }

    public int getGroupIndex() {
        return groupIndex;
    }

    public int getFeatureIndex() {
        return featureIndex;
    }

    public boolean getFeatureState() {
        return featureState;
    }
}
