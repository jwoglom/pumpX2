package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.CgmRiseFallAlertResponse;

import org.apache.commons.lang3.Validate;

/**
 * Sets a CGM rise or fall rate alert threshold.
 *
 * Cargo layout (4 bytes, little-endian):
 *   [0]  alertType  uint8  - 0=RISE, 1=FALL
 *   [1]  enable     uint8  - 0=disabled, 1=enabled
 *   [2]  mgPerDl    uint8  - rate threshold in mg/dL/min
 *   [3]  bitmask    uint8  - change bitmask (bits: 0=enabled, 1=minValue)
 */
@MessageProps(
    opCode=-60,
    size=4,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=CgmRiseFallAlertResponse.class,
    signed=true
)
public class CgmRiseFallAlertRequest extends Message {
    // alertType values
    public static final int ALERT_TYPE_RISE = 0;
    public static final int ALERT_TYPE_FALL = 1;

    private int alertType;
    private boolean enable;
    private int mgPerDl;
    private int bitmask;

    public CgmRiseFallAlertRequest() {}

    public CgmRiseFallAlertRequest(int alertType, boolean enable, int mgPerDl, int bitmask) {
        this.alertType = alertType;
        this.enable = enable;
        this.mgPerDl = mgPerDl;
        this.bitmask = bitmask;
        this.cargo = buildCargo(alertType, enable, mgPerDl, bitmask);
    }

    public static byte[] buildCargo(int alertType, boolean enable, int mgPerDl, int bitmask) {
        return Bytes.combine(
            new byte[]{ (byte) alertType },
            new byte[]{ (byte) (enable ? 1 : 0) },
            new byte[]{ (byte) mgPerDl },
            new byte[]{ (byte) bitmask }
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.alertType = raw[0] & 0xFF;
        this.enable = (raw[1] & 0xFF) != 0;
        this.mgPerDl = raw[2] & 0xFF;
        this.bitmask = raw[3] & 0xFF;
    }

    public int getAlertType() {
        return alertType;
    }

    public boolean isEnable() {
        return enable;
    }

    public int getMgPerDl() {
        return mgPerDl;
    }

    public int getBitmask() {
        return bitmask;
    }
}
