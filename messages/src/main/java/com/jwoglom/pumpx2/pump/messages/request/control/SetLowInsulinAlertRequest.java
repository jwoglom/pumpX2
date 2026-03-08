package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetLowInsulinAlertResponse;

import org.apache.commons.lang3.Validate;

/**
 * Sets the low insulin alert threshold.
 *
 * Cargo layout (1 byte):
 *   [0]  insulinThreshold  uint8  - low insulin threshold (truncated from short to 1 byte)
 *
 * Note: The decompiled app uses Short_ExtKt.toByteArray(short) which returns a single byte
 * containing the low-order byte of the short value.
 */
@MessageProps(
    opCode=-34,
    size=1,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=SetLowInsulinAlertResponse.class,
    signed=true
)
public class SetLowInsulinAlertRequest extends Message {
    private int insulinThreshold;

    public SetLowInsulinAlertRequest() {}

    public SetLowInsulinAlertRequest(int insulinThreshold) {
        this.insulinThreshold = insulinThreshold;
        this.cargo = buildCargo(insulinThreshold);
    }

    public static byte[] buildCargo(int insulinThreshold) {
        return new byte[]{ (byte) insulinThreshold };
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.insulinThreshold = raw[0] & 0xFF;
    }

    public int getInsulinThreshold() {
        return insulinThreshold;
    }
}
