package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetSiteChangeReminderResponse;

import org.apache.commons.lang3.Validate;

/**
 * Sets a site change reminder configuration.
 *
 * Cargo layout (7 bytes, little-endian):
 *   [0]   enable       uint8   - 0=disabled, 1=enabled
 *   [1]   dayCount     uint8   - number of days between site changes (truncated from short to 1 byte)
 *   [2-5] timeOfDay    uint32  - time of day in minutes since midnight
 *   [6]   bitmask      uint8   - change bitmask (bits: 0=enable, 1=dayCount, 2=timeOfDay)
 *
 * Note: dayCount uses Short_ExtKt.toByteArray(short) which returns a single byte (low-order byte).
 */
@MessageProps(
    opCode=-36,
    size=7,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=SetSiteChangeReminderResponse.class,
    signed=true
)
public class SetSiteChangeReminderRequest extends Message {
    private boolean enable;
    private int dayCount;
    private long timeOfDayMinutes;
    private int bitmask;

    public SetSiteChangeReminderRequest() {}

    public SetSiteChangeReminderRequest(boolean enable, int dayCount, long timeOfDayMinutes, int bitmask) {
        this.enable = enable;
        this.dayCount = dayCount;
        this.timeOfDayMinutes = timeOfDayMinutes;
        this.bitmask = bitmask;
        this.cargo = buildCargo(enable, dayCount, timeOfDayMinutes, bitmask);
    }

    public static byte[] buildCargo(boolean enable, int dayCount, long timeOfDayMinutes, int bitmask) {
        return Bytes.combine(
            new byte[]{ (byte) (enable ? 1 : 0) },
            new byte[]{ (byte) dayCount },
            Bytes.toUint32(timeOfDayMinutes),
            new byte[]{ (byte) bitmask }
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.enable = (raw[0] & 0xFF) != 0;
        this.dayCount = raw[1] & 0xFF;
        this.timeOfDayMinutes = Bytes.readUint32(raw, 2);
        this.bitmask = raw[6] & 0xFF;
    }

    public boolean isEnable() {
        return enable;
    }

    public int getDayCount() {
        return dayCount;
    }

    public long getTimeOfDayMinutes() {
        return timeOfDayMinutes;
    }

    public int getBitmask() {
        return bitmask;
    }
}
