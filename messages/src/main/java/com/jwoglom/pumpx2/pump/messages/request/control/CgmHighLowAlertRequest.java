package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.CgmHighLowAlertResponse;

import org.apache.commons.lang3.Validate;

/**
 * Sets a CGM high or low alert threshold.
 *
 * Cargo layout (7 bytes, little-endian):
 *   [0-1] threshold         uint16  - alert threshold in mg/dL
 *   [2-3] repeatDuration    uint16  - repeat duration in minutes
 *   [4]   alertType         uint8   - 0=HIGH, 1=LOW
 *   [5]   enableAlert       uint8   - 0=disabled, 1=enabled
 *   [6]   bitmask           uint8   - change bitmask (bits: 0=enabled, 1=threshold, 2=repeatDuration)
 */
@MessageProps(
    opCode=-62,
    size=7,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=CgmHighLowAlertResponse.class,
    signed=true
)
public class CgmHighLowAlertRequest extends Message {
    // alertType values
    public static final int ALERT_TYPE_HIGH = 0;
    public static final int ALERT_TYPE_LOW = 1;

    private int threshold;
    private int repeatDurationMinutes;
    private int alertType;
    private boolean enableAlert;
    private int bitmask;

    public CgmHighLowAlertRequest() {}

    public CgmHighLowAlertRequest(int alertType, int threshold, int repeatDurationMinutes, boolean enableAlert, int bitmask) {
        this.alertType = alertType;
        this.threshold = threshold;
        this.repeatDurationMinutes = repeatDurationMinutes;
        this.enableAlert = enableAlert;
        this.bitmask = bitmask;
        this.cargo = buildCargo(alertType, threshold, repeatDurationMinutes, enableAlert, bitmask);
    }

    public static byte[] buildCargo(int alertType, int threshold, int repeatDurationMinutes, boolean enableAlert, int bitmask) {
        return Bytes.combine(
            Bytes.firstTwoBytesLittleEndian(threshold),
            Bytes.firstTwoBytesLittleEndian(repeatDurationMinutes),
            new byte[]{ (byte) alertType },
            new byte[]{ (byte) (enableAlert ? 1 : 0) },
            new byte[]{ (byte) bitmask }
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.threshold = Bytes.readShort(raw, 0);
        this.repeatDurationMinutes = Bytes.readShort(raw, 2);
        this.alertType = raw[4] & 0xFF;
        this.enableAlert = (raw[5] & 0xFF) != 0;
        this.bitmask = raw[6] & 0xFF;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getRepeatDurationMinutes() {
        return repeatDurationMinutes;
    }

    public int getAlertType() {
        return alertType;
    }

    public boolean isEnableAlert() {
        return enableAlert;
    }

    public int getBitmask() {
        return bitmask;
    }
}
