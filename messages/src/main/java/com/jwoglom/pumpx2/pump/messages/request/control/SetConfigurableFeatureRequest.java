package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetConfigurableFeatureResponse;

import org.apache.commons.lang3.Validate;

/**
 * Turns a single pump feature on or off. Each feature is identified by a
 * (group, feature) index pair, and this message sets that feature's enabled state.
 *
 * Known use: enabling CGM support on the pump while setting up an Abbott FreeStyle
 * Libre 3 sensor. Only the t:slim X2 sends this; the Mobi pump does not use it.
 *
 * This is a one-way command. There is no companion message to read a feature's
 * current state back from the pump.
 *
 * Cargo layout (3 bytes):
 *   [0] groupIndex   uint8
 *   [1] featureIndex uint8
 *   [2] featureState uint8  - 0 = disabled, 1 = enabled
 *
 * Note: this is a different message from {@link SetRuntimeConfigRequest}, even though the two
 * names look similar.
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
