package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetAutoOffAlertResponse;

import org.apache.commons.lang3.Validate;

/**
 * Sets the Auto-Off alert configuration.
 *
 * Cargo layout (4 bytes, little-endian):
 *   [0]   enableAutoOff    uint8   - 0=disabled, 1=enabled
 *   [1-2] autoOffDuration  uint16  - duration in minutes before auto-off
 *   [3]   bitmask          uint8   - change bitmask (bits: 0=enableAutoOff, 1=autoOffDuration)
 */
@MessageProps(
    opCode=-32,
    size=4,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=SetAutoOffAlertResponse.class,
    signed=true
)
public class SetAutoOffAlertRequest extends Message {
    private boolean enableAutoOff;
    private int autoOffDuration;
    private int bitmask;

    public SetAutoOffAlertRequest() {}

    public SetAutoOffAlertRequest(boolean enableAutoOff, int autoOffDuration, int bitmask) {
        this.enableAutoOff = enableAutoOff;
        this.autoOffDuration = autoOffDuration;
        this.bitmask = bitmask;
        this.cargo = buildCargo(enableAutoOff, autoOffDuration, bitmask);
    }

    public static byte[] buildCargo(boolean enableAutoOff, int autoOffDuration, int bitmask) {
        return Bytes.combine(
            new byte[]{ (byte) (enableAutoOff ? 1 : 0) },
            Bytes.firstTwoBytesLittleEndian(autoOffDuration),
            new byte[]{ (byte) bitmask }
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.enableAutoOff = (raw[0] & 0xFF) != 0;
        this.autoOffDuration = Bytes.readShort(raw, 1);
        this.bitmask = raw[3] & 0xFF;
    }

    public boolean isEnableAutoOff() {
        return enableAutoOff;
    }

    public int getAutoOffDuration() {
        return autoOffDuration;
    }

    public int getBitmask() {
        return bitmask;
    }
}
