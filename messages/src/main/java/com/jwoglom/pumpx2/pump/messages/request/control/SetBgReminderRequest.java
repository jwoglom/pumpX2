package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetBgReminderResponse;

import org.apache.commons.lang3.Validate;

/**
 * Sets a blood glucose reminder configuration.
 *
 * Cargo layout (9 bytes, little-endian):
 *   [0]   reminderType        uint8   - 0=HIGH, 1=LOW, 2=AFTER_BOLUS
 *   [1]   enabledBGReminder   uint8   - 0=disabled, 1=enabled
 *   [2-3] reminderThreshold   uint16  - BG threshold in mg/dL
 *   [4-7] reminderMinutes     uint32  - reminder time in minutes
 *   [8]   bitmask             uint8   - change bitmask (bits: 0=enabled, 1=threshold, 2=minutes)
 */
@MessageProps(
    opCode=-40,
    size=9,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=SetBgReminderResponse.class,
    signed=true
)
public class SetBgReminderRequest extends Message {
    // reminderType values
    public static final int REMINDER_TYPE_HIGH = 0;
    public static final int REMINDER_TYPE_LOW = 1;
    public static final int REMINDER_TYPE_AFTER_BOLUS = 2;

    private int reminderType;
    private boolean enabledBGReminder;
    private int reminderThreshold;
    private long reminderMinutes;
    private int bitmask;

    public SetBgReminderRequest() {}

    public SetBgReminderRequest(int reminderType, boolean enabledBGReminder, int reminderThreshold, long reminderMinutes, int bitmask) {
        this.reminderType = reminderType;
        this.enabledBGReminder = enabledBGReminder;
        this.reminderThreshold = reminderThreshold;
        this.reminderMinutes = reminderMinutes;
        this.bitmask = bitmask;
        this.cargo = buildCargo(reminderType, enabledBGReminder, reminderThreshold, reminderMinutes, bitmask);
    }

    public static byte[] buildCargo(int reminderType, boolean enabledBGReminder, int reminderThreshold, long reminderMinutes, int bitmask) {
        return Bytes.combine(
            new byte[]{ (byte) reminderType },
            new byte[]{ (byte) (enabledBGReminder ? 1 : 0) },
            Bytes.firstTwoBytesLittleEndian(reminderThreshold),
            Bytes.toUint32(reminderMinutes),
            new byte[]{ (byte) bitmask }
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.reminderType = raw[0] & 0xFF;
        this.enabledBGReminder = (raw[1] & 0xFF) != 0;
        this.reminderThreshold = Bytes.readShort(raw, 2);
        this.reminderMinutes = Bytes.readUint32(raw, 4);
        this.bitmask = raw[8] & 0xFF;
    }

    public int getReminderType() {
        return reminderType;
    }

    public boolean isEnabledBGReminder() {
        return enabledBGReminder;
    }

    public int getReminderThreshold() {
        return reminderThreshold;
    }

    public long getReminderMinutes() {
        return reminderMinutes;
    }

    public int getBitmask() {
        return bitmask;
    }
}
