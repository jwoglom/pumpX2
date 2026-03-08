package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.CgmOutOfRangeAlertResponse;

import org.apache.commons.lang3.Validate;

/**
 * Sets the CGM out-of-range alert configuration.
 *
 * Cargo layout (3 bytes, little-endian):
 *   [0]  enable      uint8  - 0=disabled, 1=enabled
 *   [1]  alertDelay  uint8  - delay before alert (minutes)
 *   [2]  bitmask     uint8  - change bitmask (bits: 0=enabled, 1=delayBeforeAlert)
 */
@MessageProps(
    opCode=-58,
    size=3,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=CgmOutOfRangeAlertResponse.class,
    signed=true
)
public class CgmOutOfRangeAlertRequest extends Message {
    private boolean enable;
    private int alertDelay;
    private int bitmask;

    public CgmOutOfRangeAlertRequest() {}

    public CgmOutOfRangeAlertRequest(boolean enable, int alertDelay, int bitmask) {
        this.enable = enable;
        this.alertDelay = alertDelay;
        this.bitmask = bitmask;
        this.cargo = buildCargo(enable, alertDelay, bitmask);
    }

    public static byte[] buildCargo(boolean enable, int alertDelay, int bitmask) {
        return Bytes.combine(
            new byte[]{ (byte) (enable ? 1 : 0) },
            new byte[]{ (byte) alertDelay },
            new byte[]{ (byte) bitmask }
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.enable = (raw[0] & 0xFF) != 0;
        this.alertDelay = raw[1] & 0xFF;
        this.bitmask = raw[2] & 0xFF;
    }

    public boolean isEnable() {
        return enable;
    }

    public int getAlertDelay() {
        return alertDelay;
    }

    public int getBitmask() {
        return bitmask;
    }
}
