package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.SetMissedMealBolusReminderResponse;

import org.apache.commons.lang3.Validate;

/**
 * Sets a missed meal bolus reminder configuration.
 *
 * Cargo layout (8 bytes, little-endian):
 *   [0]   reminderIndex    uint8   - reminder slot index
 *   [1]   enabledReminder  uint8   - 0=disabled, 1=enabled
 *   [2-3] startTime        uint16  - start time in minutes since midnight
 *   [4-5] endTime          uint16  - end time in minutes since midnight
 *   [6]   selectedDays     uint8   - days-of-week bitmask (bit 0=Mon, 1=Tue, 2=Wed, 3=Thu, 4=Fri, 5=Sat, 6=Sun)
 *   [7]   bitmask          uint8   - change bitmask (bits: 0=enabled, 1=startTime, 2=endTime, 3=selectedDays)
 */
@MessageProps(
    opCode=-38,
    size=8,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=SetMissedMealBolusReminderResponse.class,
    signed=true
)
public class SetMissedMealBolusReminderRequest extends Message {
    private int reminderIndex;
    private boolean enabledReminder;
    private int startTime;
    private int endTime;
    private int selectedDays;
    private int bitmask;

    public SetMissedMealBolusReminderRequest() {}

    public SetMissedMealBolusReminderRequest(int reminderIndex, boolean enabledReminder, int startTime, int endTime, int selectedDays, int bitmask) {
        this.reminderIndex = reminderIndex;
        this.enabledReminder = enabledReminder;
        this.startTime = startTime;
        this.endTime = endTime;
        this.selectedDays = selectedDays;
        this.bitmask = bitmask;
        this.cargo = buildCargo(reminderIndex, enabledReminder, startTime, endTime, selectedDays, bitmask);
    }

    public static byte[] buildCargo(int reminderIndex, boolean enabledReminder, int startTime, int endTime, int selectedDays, int bitmask) {
        return Bytes.combine(
            new byte[]{ (byte) reminderIndex },
            new byte[]{ (byte) (enabledReminder ? 1 : 0) },
            Bytes.firstTwoBytesLittleEndian(startTime),
            Bytes.firstTwoBytesLittleEndian(endTime),
            new byte[]{ (byte) selectedDays },
            new byte[]{ (byte) bitmask }
        );
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.reminderIndex = raw[0] & 0xFF;
        this.enabledReminder = (raw[1] & 0xFF) != 0;
        this.startTime = Bytes.readShort(raw, 2);
        this.endTime = Bytes.readShort(raw, 4);
        this.selectedDays = raw[6] & 0xFF;
        this.bitmask = raw[7] & 0xFF;
    }

    public int getReminderIndex() {
        return reminderIndex;
    }

    public boolean isEnabledReminder() {
        return enabledReminder;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getSelectedDays() {
        return selectedDays;
    }

    public int getBitmask() {
        return bitmask;
    }
}
